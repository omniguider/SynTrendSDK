package com.omni.syntrendsdk.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.omni.syntrendsdk.R;
import com.omni.syntrendsdk.module.OmniClusterItem;
import com.omni.syntrendsdk.tool.Tools;

public class OmniClusterRender extends DefaultClusterRenderer<OmniClusterItem> {

    private IconGenerator mClusterIconGenerator;
    private Context mContext;

    public OmniClusterRender(Context context, GoogleMap map, ClusterManager<OmniClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(OmniClusterItem item, MarkerOptions markerOptions) {
        if (item.getPOI() == null) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getIconRes()));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getIconRes()));
        }
        markerOptions.title(item.getTitle());
//        markerOptions.snippet(item.getSnippet());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<OmniClusterItem> cluster, MarkerOptions markerOptions) {
        IconGenerator TextMarkerGen = new IconGenerator(mContext);

//        final Drawable clusterIcon = Tools.getInstance().getDrawable(mContext, R.mipmap.poi_aed);
//        clusterIcon.setColorFilter(Tools.getInstance().getColor(mContext, android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);
//        TextMarkerGen.setBackground(clusterIcon);

        Drawable marker = mContext.getResources().getDrawable(R.drawable.solid_circle_holo_orange_light);
        TextMarkerGen.setBackground(marker);

        TextMarkerGen.makeIcon(cluster.getSize() + "");
        TextMarkerGen.setTextAppearance(mContext,
                cluster.getSize() < 10 ?
                        R.style.ClusterViewTextAppearanceBig :
                        R.style.ClusterViewTextAppearanceMedium);
        if (cluster.getSize() >= 10) {
            TextMarkerGen.setContentPadding(Tools.getInstance().dpToIntPx(mContext, 10),
                    Tools.getInstance().dpToIntPx(mContext, 8),
                    Tools.getInstance().dpToIntPx(mContext, 10),
                    Tools.getInstance().dpToIntPx(mContext, 8));
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(TextMarkerGen.makeIcon());
        markerOptions.icon(icon);
    }

    @Override
    protected void onClusterRendered(Cluster<OmniClusterItem> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);

    }

}
