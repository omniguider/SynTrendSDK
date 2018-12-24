package com.omni.syntrendsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.omni.syntrendsdk.manager.DataCacheManager
import com.omni.syntrendsdk.module.BuildingFloor
import com.omni.syntrendsdk.module.POI
import com.omni.syntrendsdk.network.LocationApi
import com.omni.syntrendsdk.network.NetworkManager
import com.omni.syntrendsdk.tool.SynTrendText
import java.util.*


class PoiSearchActivity : Activity() {

    private var mView: View? = null

    private var noDataTv: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var mPoiList: MutableList<POI>? = null
    private var poiListAdapter: PoiListAdapter? = null
    private var mSearchText: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.poi_search_fragment_view)
        initView()
    }

    private fun initView() {

        recyclerView = findViewById(R.id.drawer_exhibits_fragment_view_rv)

        findViewById<RelativeLayout>(R.id.activity_search_rl).setBackgroundResource(R.color.black_3d)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = layoutManager

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.color.gray_d8)!!)
        recyclerView!!.addItemDecoration(divider)
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        val toolbar = findViewById<Toolbar>(R.id.hint_action_bar)
        toolbar.setBackgroundResource(android.R.color.black)
        val titleTV = toolbar.findViewById(R.id.fragment_news_detail_tv_title) as TextView
        titleTV.setText(R.string.activity_main_title_guide)
        val backTV = toolbar.findViewById(R.id.fragment_news_detail_tv_back) as FrameLayout
        backTV.setOnClickListener { finish() }

        val searchEdt = findViewById<EditText>(R.id.map_content_view_tv_search)
        val searchBtn = findViewById<ImageView>(R.id.search_quest_btn)
        searchBtn.setOnClickListener {
            val searchText = searchEdt.text.toString()
            mSearchText = searchText
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
            doSearchPOI(mSearchText)
        }
        searchEdt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = searchEdt.text.toString()
                mSearchText = searchText
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
                doSearchPOI(mSearchText)
                return@OnEditorActionListener true
            }
            false
        })

        noDataTv = findViewById(R.id.no_data_tv)

    }

    internal inner class PoiListAdapter(private val clusterItems: List<POI>) : RecyclerView.Adapter<PoiListAdapter.mViewHolder>() {
        inner class mViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var icon: ImageView = itemView.findViewById(R.id.search_list_item_view_iv) as ImageView
            var title: TextView = itemView.findViewById(R.id.search_list_item_view_tv_title) as TextView
            var floor: TextView = itemView.findViewById(R.id.search_list_item_view_tv_floor) as TextView
            var mainLayout: LinearLayout = itemView.findViewById(R.id.search_list_item_view) as LinearLayout
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_list_item_view, parent, false)
            return mViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: mViewHolder, position: Int) {
            val poi = clusterItems[position]
            holder.icon.setImageResource(poi.getPOIIconRes(false))
            holder.title.text = poi.name
            val floor = DataCacheManager.getInstance().getSearchFloorPlanId(this@PoiSearchActivity, poi.id)
            holder.floor.text = floor.name
            holder.mainLayout.setOnClickListener {
                val intent = Intent()
                intent.putExtra(SynTrendText.INTENT_EXTRAS_SELECTED_POI, poi)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        override fun getItemCount(): Int {
            return clusterItems.size
        }
    }

    private fun doSearchPOI(keyword: String?) {
        Log.e("OKOK", "doSearchPOI")
        var id = DataCacheManager.getInstance().userCurrentFloorPlanId!!
        if (id == "outdoor")
            id = DataCacheManager.getInstance().getMainGroundFloorPlanId(this).floorPlanId
        val buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(this, id)
        if (buildingId != null) {
            LocationApi.getInstance().doSearch(this, buildingId, keyword!!, object : NetworkManager.NetworkManagerListener<Array<BuildingFloor>> {

                override fun onSucceed(response: Array<BuildingFloor>?) {
                    mPoiList = ArrayList()
                    for (buildingFloor in response!!) {
                        for (poi in buildingFloor.pois!!) {
                            if (poi.type != "map_text") {
                                mPoiList!!.add(poi)
                            }
                        }
                    }
                    runOnUiThread { setRVData(mPoiList!!) }
                }

                override fun onFail(errorMsg: String?, shouldRetry: Boolean) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }


    private fun setRVData(poiList: List<POI>) {
        if (poiList.isEmpty()) {
            noDataTv!!.visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
        } else {
            noDataTv!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
        }

        poiListAdapter = PoiListAdapter(poiList)
        recyclerView!!.adapter = poiListAdapter
        poiListAdapter!!.notifyDataSetChanged()
    }
}
