package riza.com.cto.view.local.check

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_check.*
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.data.db.Area
import riza.com.cto.support.debugLog
import riza.com.cto.support.getCompatColor
import riza.com.cto.support.openFolder
import riza.com.cto.support.visible

class CheckActivity : AppCompatActivity(), OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    private lateinit var mMap: GoogleMap

    private var mPolygon : Polygon? = null
    private var mCircle : Circle? = null
    private var mMarkers = arrayListOf<Marker>()

    private val vm by lazy { ViewModelProvider(this).get(CheckVM::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun receiveExtra() {
        intent?.getParcelableExtra<Area>("area")?.let {
            vm.setPolygonData(it)
        }
    }

    private fun initView() {

        btn_back?.setOnClickListener { onBackPressed() }

        seek_radius?.setOnSeekBarChangeListener(this)
        seek_user?.setOnSeekBarChangeListener(this)

        btn_single?.setOnClickListener {
            if (vm.nUser.value ?: 0 > 0 && vm.radius.value ?: 0 > 0) {
                vm.generateSingleTest()
            } else {
                toast("Radius & User couldn't be 0")
            }
        }

        btn_save?.setOnClickListener {
            vm.saveToCSV()
        }

        rg_algorithm?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rad_wn -> vm.setisUsingWN(true)
                R.id.rad_cn -> vm.setisUsingWN(false)
            }

        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

    }

    private fun initObserver() {
        vm.polygonData.observe(this, Observer {
            debugLog(it)
/*
            if(it.size >= 3){
                if(mPolygon == null) createPolygon(it)
                else mPolygon?.points = it

                mPolygon?.isVisible = true
            }else{
                mPolygon?.isVisible = false
            }*/
        })

        vm.triangle.observe(this, Observer {
            debugLog("triangles")
            debugLog(it)

            it.forEach {
                createPolygon(
                    data = it.points,
                    color = R.color.colorPrimary
                )
            }
        })

        vm.centroid.observe(this, Observer {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 13f))

        })

        vm.displayRadius.observe(this, Observer {
            mCircle?.remove()
            createCircle(vm.centroid.value!!, it)
        })

        vm.listTest.observe(this, Observer {
            mMarkers.forEach { it.remove() }
            it.forEach { addMarkerOn(it.first, it.second) }
        })

        vm.radius.observe(this, Observer {
//            seek_radius?.progress = it
            tv_radius?.text = "$it Meters"
        })

        vm.nUser.observe(this, Observer {
//            seek_user?.progress = it
            tv_n_user?.text = "$it Users"
        })

        vm.milis.observe(this, Observer {

            tv_milis?.visible()
            tv_milis?.text = "$it ms"

        })

        vm.message.observe(this, Observer {
            toast(it)
        })

        vm.outputFolder.observe(this, Observer {
            openFolder(
                Uri.parse(it.toString())
            )
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initView()
        initObserver()
        receiveExtra()
    }

    private fun createPolygon(data: List<LatLng>, color: Int = R.color.polygonColor) {

        mPolygon = mMap.addPolygon(

            PolygonOptions()
                .fillColor(getCompatColor(color))
                .strokeWidth(1f)
                .addAll(data)
                .strokeJointType(JointType.BEVEL)

        )
        debugLog(mPolygon)
    }

    private fun addMarkerOn(p: LatLng, boolean: Boolean) {

        val icon = if(boolean) R.drawable.pointer_green else R.drawable.pointer_red

        val marker = mMap.addMarker(
            MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(icon)).title(p.toString())
        )
        mMarkers.add(marker)

    }

    private fun createCircle(center: LatLng, radius: Double){

        mCircle = mMap.addCircle( CircleOptions()
            .center(center)
            .radius(radius)
            .fillColor(Color.TRANSPARENT)
            .strokeWidth(1f)
        )

    }

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if(fromUser){
            when(p0?.id){
                R.id.seek_radius-> vm.setRadius(progress)
                R.id.seek_user-> vm.setNUser(progress)
            }
        }

    }

    override fun onStartTrackingTouch(p0: SeekBar?) = Unit

    override fun onStopTrackingTouch(p0: SeekBar?) = Unit


}
