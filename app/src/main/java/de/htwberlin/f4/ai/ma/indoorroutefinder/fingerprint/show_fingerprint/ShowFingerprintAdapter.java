package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.show_fingerprint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;

/**
 * Created by Johann Winter
 *
 * Adapter to fill the ListView in ShowFingerprintActivity with fingerprint data
 */


class ShowFingerprintAdapter extends BaseExpandableListAdapter{

    private Fingerprint fingerprint;
    private Context context;

    ShowFingerprintAdapter(Context context, Fingerprint fingerprint) {
        this.context = context;
        this.fingerprint = fingerprint;
    }


    @Override
    public int getGroupCount() {
        return fingerprint.getSignalSampleList().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return fingerprint.getSignalSampleList().get(i).getAccessPointInformationList().size();
    }

    @Override
    public Object getGroup(int i) {
        return fingerprint.getSignalSampleList().get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return fingerprint.getSignalSampleList().get(i).getAccessPointInformationList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        TextView textView = new TextView(context);
        textView.setText((i+1) + ". Sekunde");
        textView.setPadding(100, 0, 0, 0);
        textView.setTextSize(20);
        return textView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        textView.setText(fingerprint.getSignalSampleList().get(i).getAccessPointInformationList().get(i1).getMacAddress() +
                "   " + fingerprint.getSignalSampleList().get(i).getAccessPointInformationList().get(i1).getRssi() + " dBm");
        textView.setPadding(140, 0, 0, 0);
        return textView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
