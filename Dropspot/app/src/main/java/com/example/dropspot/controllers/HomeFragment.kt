package com.example.dropspot.controllers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.dropspot.R
import com.example.dropspot.databinding.HomeFragmentBinding
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.viewmodels.HomeViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Order
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.text.NumberFormat
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback, PermissionListener {
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: HomeFragmentBinding

    // google maps api
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mapFragment: SupportMapFragment? = null
    private lateinit var gcd: Geocoder
    private var posMarker: Marker? = null

    private

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    // new spot
    private var newSpotMarker: Marker? = null
    private var newSpotLatitude: Double? = null
    private var newSpotLongitude: Double? = null


    //field validation
    private val validator = Validator(this)

    // street val
    @NotEmpty(messageResId = R.string.spot_name_req)
    @Order(1)
    private lateinit var inputName: EditText

    // park val
    @Order(2)
    @NotEmpty(messageResId = R.string.street_req)
    private lateinit var inputStreet: EditText

    @Order(3)
    @NotEmpty(messageResId = R.string.house_number_req)
    private lateinit var inputNumber: EditText

    @Order(4)
    @NotEmpty(messageResId = R.string.city_req)
    private lateinit var inputCity: EditText

    @Order(5)
    @NotEmpty(messageResId = R.string.postal_code_req)
    private lateinit var inputPostal: EditText

    @Order(6)
    @NotEmpty(messageResId = R.string.state_req)
    private lateinit var inputState: EditText

    @Order(7)
    @NotEmpty(messageResId = R.string.country_req)
    private lateinit var inputCountry: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setupUI()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        validator.validationMode = Validator.Mode.IMMEDIATE
        validator.setValidationListener(object :
            MyValidationListener(this.requireContext(), this.requireView()) {
            override fun onValidationSucceeded() {
                addSpot()
            }

        })
        initMap()

        // sets coords if new spot marker was already added in session
        val coords = savedInstanceState?.getDoubleArray("NEW_SPOT_MARKER_COORDS")
        if (coords != null) {
            newSpotLatitude = coords[0]
            newSpotLongitude = coords[1]
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("home", "saveinstance")
        // saves coords of new spot marker
        if (newSpotMarker != null) {
            outState.putDoubleArray(
                "NEW_SPOT_MARKER_COORDS",
                doubleArrayOf(
                    newSpotMarker!!.position.latitude,
                    newSpotMarker!!.position.longitude
                )
            )
        }
    }

    private fun setupUI() {

        // fab
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener {
            fab.isExpanded = true
        }

        // collapse add spot view
        binding.collapse.setOnClickListener {
            fab.isExpanded = false
        }

        // add btn
        binding.btnAdd.setOnClickListener {
            if (binding.toggleSpotSort.checkedButtonId == R.id.toggle_street) {
                Log.i("home", "street validation")
                validator.validateBefore(inputStreet)
            } else {
                Log.i("home", "park validation")
                validator.validate()
            }
        }

        // spot sort toggle
        binding.toggleSpotSort.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.toggle_street -> binding.groupPark.visibility = View.GONE
                R.id.toggle_park -> binding.groupPark.visibility = View.VISIBLE
            }
        }
        binding.toggleSpotSort.check(R.id.toggle_street)

        // dropdown indoor/outdoor
        val park_cat =
            arrayOf("Indoor", "Outdoor", "Out & Indoor")

        val dropdown_adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            park_cat
        )

        binding.dropdownParkCategory.setAdapter(dropdown_adapter)


        // fee slider
        binding.sliderFee.setLabelFormatter {
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 2
            format.currency = Currency.getInstance("EUR")
            format.format(it.toDouble())
        }

        // add spot fields
        inputName = binding.inputName
        inputStreet = binding.inputStreet
        inputNumber = binding.inputHouseNumber
        inputCity = binding.inputCity
        inputPostal = binding.inputPostalCode
        inputState = binding.inputState
        inputCountry = binding.inputCountry

        //add spot response handling
        viewModel.addParkSpotSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val message = if (it) resources.getString(R.string.spot_added) else
                resources.getString(R.string.failed_to_add_spot)
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        })

        viewModel.addParkSpotSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val message = if (it) resources.getString(R.string.spot_added) else
                resources.getString(R.string.failed_to_add_spot)
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        })

    }

    private fun addSpot() {
        val markerIsSet: Boolean = this.newSpotLatitude != null && this.newSpotLongitude != null
        val categoryIsNotEmpty: Boolean = binding.dropdownParkCategory.text.isNotEmpty()
        val streetToggleIsSet: Boolean =
            binding.toggleSpotSort.checkedButtonId == R.id.toggle_street
        if (markerIsSet) {

            if (streetToggleIsSet) {

                viewModel.addStreetSpot(
                    inputName.text.toString().trim()
                    , this.newSpotLatitude!!
                    , this.newSpotLongitude!!
                )

            } else {

                if (categoryIsNotEmpty) {
                    viewModel.addParkSpot(
                        inputName.text.toString().trim()
                        , this.newSpotLatitude!!
                        , this.newSpotLongitude!!
                        , inputStreet.text.toString().trim()
                        , inputNumber.text.toString().trim()
                        , inputCity.text.toString().trim()
                        , inputPostal.text.toString().trim()
                        , inputState.text.toString().trim()
                        , inputCountry.text.toString().trim()
                        , binding.dropdownParkCategory.text.toString().trim()
                        , binding.sliderFee.value.toDouble()
                    )
                } else {
                    binding.layoutParkCategory.error =
                        resources.getString(R.string.no_park_cat_selected)
                }
            }

        } else {
            Snackbar.make(this.requireView(), R.string.mark_new_spot_please, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    // map
    private fun initMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        if (mapFragment == null) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            ft?.replace(R.id.map, mapFragment as SupportMapFragment)?.commit()
        }
        fusedLocationProviderClient = FusedLocationProviderClient(activity!!)
        mapFragment!!.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("home", "map ready")
        mMap = googleMap

        // sets map type
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // sets marker if already created in session
        if (newSpotLatitude != null && newSpotLongitude != null) {
            Log.i("home", "redraw marker")
            drawNewSpotMarker(newSpotLatitude!!, newSpotLongitude!!)
        }

        // sets current pos on map
        setCurrentPos()

        // handles marker dragging
        mMap.setOnMarkerDragListener(
            object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragEnd(m: Marker) {
                    m.setIcon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_flag_secondary
                        )
                    )
                    updateNewSpotLocation(m.position.latitude, m.position.longitude)
                }

                override fun onMarkerDragStart(m: Marker) {
                    m.setIcon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_flag_thick
                        )
                    )
                }

                override fun onMarkerDrag(p0: Marker) {
                }
            }
        )


        // handles map clicks for new spot creation
        mMap.setOnMapClickListener {
            if (binding.fab.isExpanded && newSpotMarker == null) {
                Log.i("home", "marker creation")

                drawNewSpotMarker(it.latitude, it.longitude)
                updateNewSpotLocation(it.latitude, it.longitude)
            }
        }

        // handles camera movement
        mMap.setOnCameraIdleListener {
            val visibleRegion = mMap.projection.visibleRegion

            val center = visibleRegion.latLngBounds.center
            val farLeft = visibleRegion.farLeft
            val nearRight = visibleRegion.nearRight

            val diagonalDistance = FloatArray(1)

            Location.distanceBetween(
                farLeft.latitude,
                farLeft.longitude,
                nearRight.latitude,
                nearRight.longitude,
                diagonalDistance
            )

            val radiusInMeter = diagonalDistance[0] / 2

            viewModel.setCameraCenterAndRadius(center, radiusInMeter.toDouble())
        }

        //handles user movement
    }

    private fun drawNewSpotMarker(latitude: Double, longitude: Double) {
        newSpotMarker = mMap.addMarker(
            com.google.android.gms.maps.model.MarkerOptions()
                .draggable(true)
                .position(
                    com.google.android.gms.maps.model.LatLng(
                        latitude,
                        longitude
                    )
                )
                .title("New Spot")
                .icon(
                    bitmapDescriptorFromVector(
                        this.requireContext(),
                        com.example.dropspot.R.drawable.ic_flag_secondary
                    )
                )
        )
        Log.i("home", "marker:($latitude,$longitude)")
        binding.flag.setImageResource(R.drawable.ic_outlined_flag_24px)
    }

    private fun updateNewSpotLocation(latitude: Double, longitude: Double) {
        val possibleAddresses = gcd.getFromLocation(latitude, longitude, 10)
        setAddressFields(possibleAddresses)
        possibleAddresses.forEach {
            Log.i("address", it.toString())
        }
        newSpotLatitude = latitude
        newSpotLongitude = longitude
    }

    private fun setAddressFields(possibleAddresses: List<Address>) {
        val mostPossibleAddress: Address = possibleAddresses[0]
        binding.inputStreet.setText(mostPossibleAddress.thoroughfare ?: "")
        binding.inputHouseNumber.setText(mostPossibleAddress.featureName ?: "")
        binding.inputCity.setText(mostPossibleAddress.locality ?: "")
        binding.inputPostalCode.setText(mostPossibleAddress.postalCode ?: "")
        binding.inputState.setText(mostPossibleAddress.subAdminArea ?: "")
        binding.inputCountry.setText(mostPossibleAddress.countryName ?: "")
    }

    private fun setCurrentPos() {
        if (isPermissionGiven()) {
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true

            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    private fun isPermissionGiven(): Boolean {
        return ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        Dexter.withActivity(activity!!)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check()
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(context!!).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            activity!!,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful && task.result != null) {
                val mLastLocation = task.result

                var address = "No known address"
                gcd = Geocoder(context, Locale.getDefault())

                val addresses: List<Address>
                try {
                    addresses =
                        gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation.longitude, 1)
                    if (addresses.isNotEmpty()) {
                        address = addresses[0].getAddressLine(0)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                posMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                        .title("Current Location")
                        .snippet(address)
                        .icon(
                            bitmapDescriptorFromVector(
                                this.requireContext(),
                                R.drawable.ic_skater
                            )
                        ) // custom position marker
                )

                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                    .zoom(17f)
                    .build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            } else {
                Snackbar.make(
                    this.requireView(),
                    "Current location not found",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    // permissionListener
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
            permission: PermissionRequest?,
            token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Snackbar.make(
            this.requireView(),
            "Permission required for showing location",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
