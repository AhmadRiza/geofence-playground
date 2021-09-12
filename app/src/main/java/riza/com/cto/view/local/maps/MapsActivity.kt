package riza.com.cto.view.local.maps

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.support.debugLog
import riza.com.cto.support.getCompatColor

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var isDrawing = true
    private var mPolygon : Polygon? = null
    private var mMarkers = arrayListOf<Marker>()

    private val vm by lazy { ViewModelProvider(this).get(MapsVM::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initView()
        initObserver()
    }

    private fun initView() {
        btn_back?.setOnClickListener { onBackPressed() }
        btn_clear?.setOnClickListener { vm.clear() }
        btn_undo?.setOnClickListener { vm.undo() }
        btn_save?.setOnClickListener {

            val name = et_name?.text.toString()
            if(name.isBlank()) toast("Type the name")
            else {

                vm.getArea(name).let {

                    setResult(
                        Activity.RESULT_OK,
                        intent.apply {
                            putExtra("data", it)
                        }
                    )

                    finish()
                    return@setOnClickListener
                }
            }

        }
    }

    private fun initObserver() {
        vm.polygonData.observe(this, Observer {
            debugLog(it)

            clearMarker()

            it.forEach {
                addMarkerOn(it)
            }

            if(it.size >= 3){
                if(mPolygon == null) createPolygon(it)
                else mPolygon?.points = it

                mPolygon?.isVisible = true
            }else{
                mPolygon?.isVisible = false
            }
        })

        vm.polygonOutput.observe(this, Observer {
            debugLog(it)
        })

        vm.polygonDbId.observe(this, Observer {
            toast("Area disimpan")
            onBackPressed()

        })
    }

    private fun clearMarker() {
        mMarkers.forEach {
            it.remove()
        }
    }

    private fun addMarkerOn(p: LatLng) {

        val marker = mMap.addMarker(
            MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)).title(p.toString())
        )
        mMarkers.add(marker)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val uin = LatLng(-7.9503817,112.6063938)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uin, 18f))

        mMap.setOnMapClickListener {
            if(isDrawing) addPolygonPoint(it)
        }


    }

    private fun addPolygonPoint(it: LatLng) {
        vm.addPoint(it)
    }

    private fun createPolygon(data: List<LatLng>) {

        mPolygon = mMap.addPolygon(

            PolygonOptions()
                .fillColor(getCompatColor(R.color.polygonColor))
                .strokeWidth(1f)
                .addAll(data)
                .strokeJointType(JointType.BEVEL)

        )

        mPolygon?.tag = "drawing"

        debugLog(mPolygon)
    }


}
