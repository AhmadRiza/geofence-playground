package riza.com.cto.view.net.userlocation

import android.content.Context
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
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.model.AreaPromo
import riza.com.cto.support.getCompatColor


class UserLocationsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {


        fun getIntent(context: Context, area: List<AreaPromo>, idUser: Long) =
            context.intentFor<UserLocationsActivity>().apply {
                putExtra("idUser", idUser)
                putParcelableArrayListExtra("areas", ArrayList(area))
            }.clearTop()

    }

    private lateinit var mMap: GoogleMap

    private var mMarkers = arrayListOf<Marker>()
    private var mPolygons = arrayListOf<Polygon>()
    private var mLines = arrayListOf<Polyline>()


    private val vm by lazy { ViewModelProvider(this).get(UserLocationVM::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_user)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val uin = LatLng(-7.9503817, 112.6063938)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uin, 18f))

        initView()
        initObserver()
        initExtra()
    }

    private fun initExtra() {
        intent.getParcelableArrayListExtra<AreaPromo>("areas")?.let {
            vm.setAreaPromo(it)
        }

        intent.getLongExtra("idUser", -1L).let {
            vm.getUser(it)
        }

    }

    private fun initObserver() {

        vm.points.observe(this, Observer {

            mMarkers.forEach { it.remove() }
            mLines.forEach { it.remove() }

            it.forEachIndexed { index, locs: Pair<String, List<LatLng>> ->
                locs.second.forEach { addMarkerOn(it, locs.first) }
                createLine(index, locs.second)
            }


        })

        vm.areas.observe(this, Observer {
            mPolygons.forEach { it.remove() }
            it.forEach {
                createPolygon(it)
            }
        })

        vm.center.observe(this, Observer {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        })

        vm.error.observe(this, Observer {
            toast(it)
        })

    }

    private fun initView() {
        btn_back?.setOnClickListener { onBackPressed() }
    }

    private fun clearMarker() {
        mMarkers.forEach {
            it.remove()
        }
    }

    private fun addMarkerOn(p: LatLng, title: String) {

        val marker = mMap.addMarker(
            MarkerOptions().position(p)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)).title(title)
        )

        mMarkers.add(marker)


    }


    private fun createPolygon(data: List<LatLng>) {

        val p = mMap.addPolygon(

            PolygonOptions()
                .fillColor(getCompatColor(R.color.polygonColor))
                .strokeWidth(1f)
                .addAll(data)
                .strokeJointType(JointType.BEVEL)
        )

        mPolygons.add(p)
    }


    private fun createLine(idx: Int, data: List<LatLng>) {
        val polyline: Polyline = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .width(1f)
                .jointType(JointType.BEVEL)
                .color(vm.colors[idx])
                .addAll(
                    data
                )
        )
        mLines.add(polyline)
    }

}
