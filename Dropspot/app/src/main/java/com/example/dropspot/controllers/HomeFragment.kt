package com.example.dropspot.controllers

import android.Manifest
import android.content.Context
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
import com.example.dropspot.utils.InputLayoutTextWatcher
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.viewmodels.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Order
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.text.NumberFormat
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: HomeFragmentBinding

    // google maps api
    private var map: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mapFragment: SupportMapFragment? = null
    private lateinit var gcd: Geocoder
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted: Boolean = false
    private var lastKnownLocation: Location? = null
    private val defaultLocation: LatLng = LatLng(0.0, 0.0)

    private val spotMarkers: MutableList<Marker> = mutableListOf()

    companion object {
        private val TAG = "home"
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
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

        // sets coords if new spot marker was already added in session
        val coords = savedInstanceState?.getDoubleArray("NEW_SPOT_MARKER_COORDS")
        if (coords != null) {
            newSpotLatitude = coords[0]
            newSpotLongitude = coords[1]
        }

        setupUI()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // validation setup
        validator.validationMode = Validator.Mode.IMMEDIATE
        validator.setValidationListener(object :
            MyValidationListener(this.requireContext(), this.requireView()) {
            override fun onValidationSucceeded() {
                addSpot()
            }

        })

        // maps init
        gcd = Geocoder(context, Locale.getDefault())
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())
        initMap()


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "saveinstance")
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
        inputName.addTextChangedListener(InputLayoutTextWatcher(binding.layoutName))
        inputStreet.addTextChangedListener(InputLayoutTextWatcher(binding.layoutStreet))
        inputNumber.addTextChangedListener(InputLayoutTextWatcher(binding.layoutHouseNumber))
        inputCity.addTextChangedListener(InputLayoutTextWatcher(binding.layoutCity))
        inputPostal.addTextChangedListener(InputLayoutTextWatcher(binding.layoutPostalCode))
        inputState.addTextChangedListener(InputLayoutTextWatcher(binding.layoutState))
        inputCountry.addTextChangedListener(InputLayoutTextWatcher(binding.layoutCountry))
        binding.dropdownParkCategory.addTextChangedListener(InputLayoutTextWatcher(binding.layoutParkCategory))

        //add spot response handling
        viewModel.addParkSpotSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val message = if (it) resources.getString(R.string.park_spot_added) else
                resources.getString(R.string.failed_to_add_spot)
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
            removeNewSpotMarker()
        })

        viewModel.addStreetSpotSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val message = if (it) resources.getString(R.string.street_spot_added) else
                resources.getString(R.string.failed_to_add_spot)
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
            removeNewSpotMarker()
        })

        viewModel.spotsInRadius.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.forEach { spot ->
                if (map != null) {
                    // don't draw to map and add to markers if already added in sesh
                    if (!spotMarkers.any { drawnMarker ->
                            drawnMarker.position.latitude == spot.latitude && drawnMarker.position.longitude == spot.longitude
                        }) {
                        spotMarkers.add(
                            map!!.addMarker(
                                MarkerOptions()
                                    .position(LatLng(spot.latitude, spot.longitude))
                                    .title(spot.name)
                                    .icon(
                                        bitmapDescriptorFromVector(
                                            requireContext(),
                                            R.drawable.ic_spot_marker_colored
                                        )
                                    )
                            )
                        )
                    }

                }
            }
        }
        )
    }

    private fun removeNewSpotMarker() {
        if (newSpotMarker != null) {
            newSpotMarker!!.setIcon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.ic_spot_marker_colored
                )
            )
            spotMarkers.add(newSpotMarker!!)
            newSpotMarker = null
            newSpotLatitude = null
            newSpotLongitude = null
        }

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
        map = googleMap

        // sets map type
        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        // sets markers if already created in session
        if (newSpotLatitude != null && newSpotLongitude != null) {
            Log.i("home", "redraw marker")
            drawNewSpotMarker(newSpotLatitude!!, newSpotLongitude!!)
        }

        Log.i("home", spotMarkers.toString())
        val markers = mutableMapOf<LatLng, String>()
        spotMarkers.forEach {
            Log.i("home", it.title)
            markers.putIfAbsent(it.position, it.title)
        }

        markers.forEach { k, v ->
            spotMarkers.add(
                map!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(k.latitude, k.longitude))
                        .title(v)
                        .icon(
                            bitmapDescriptorFromVector(
                                requireContext(),
                                R.drawable.ic_spot_marker_colored
                            )
                        )
                )
            )
        }

        // handles marker dragging
        map!!.setOnMarkerDragListener(
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
        map!!.setOnMapClickListener {
            if (binding.fab.isExpanded && newSpotMarker == null) {
                Log.i("home", "marker creation")

                drawNewSpotMarker(it.latitude, it.longitude)
                updateNewSpotLocation(it.latitude, it.longitude)
            }
        }

        // handles camera movement
        map!!.setOnCameraIdleListener {
            val visibleRegion = map!!.projection.visibleRegion

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

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

    }

    private fun drawNewSpotMarker(latitude: Double, longitude: Double) {
        newSpotMarker = map!!.addMarker(
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
        var possibleAddresses: List<Address>? = null
        try {
            possibleAddresses = gcd.getFromLocation(latitude, longitude, 10)
        } catch (ex: IOException) {

        }
        setAddressFields(possibleAddresses)
        possibleAddresses?.forEach {
            Log.i("address", it.toString())
        }
        newSpotLatitude = latitude
        newSpotLongitude = longitude
    }

    private fun setAddressFields(possibleAddresses: List<Address>?) {
        val mostPossibleAddress: Address? = possibleAddresses?.get(0)
        binding.inputStreet.setText(mostPossibleAddress?.thoroughfare ?: "")
        binding.inputHouseNumber.setText(mostPossibleAddress?.featureName ?: "")
        binding.inputCity.setText(mostPossibleAddress?.locality ?: "")
        binding.inputPostalCode.setText(mostPossibleAddress?.postalCode ?: "")
        binding.inputState.setText(mostPossibleAddress?.subAdminArea ?: "")
        binding.inputCountry.setText(mostPossibleAddress?.countryName ?: "")
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

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map!!.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map!!.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}
