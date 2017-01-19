/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.zd.vpn.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

public class FaqFragment extends Fragment {
    static class FAQEntry {

        public FAQEntry(int startVersion, int endVersion, int title, int description) {
            this.startVersion = startVersion;
            this.endVersion = endVersion;
            this.description = description;
            this.title = title;
        }

        final int startVersion;
        final int endVersion;
        final int description;
        final int title;

        public boolean runningVersion() {
            if (Build.VERSION.SDK_INT >= startVersion) {
                if (Build.VERSION.SDK_INT <= endVersion)
                    return true;

                if (endVersion == -1)
                    return true;

                String release = Build.VERSION.RELEASE;
                boolean isOlderThan443 =  !release.startsWith("4.4.3")  && !release.startsWith("4.4.4") &&
                        !release.startsWith("4.4.5") && !release.startsWith("4.4.6");

                boolean isOlderThan442 = isOlderThan443 && !release.startsWith("4.4.2");


                if(Build.VERSION.SDK_INT== Build.VERSION_CODES.KITKAT) {
                    if (endVersion == -441 && isOlderThan442)
                        return true;

                    if (endVersion == -442 && isOlderThan443)
                        return true;
                } else if (endVersion == -441 || endVersion == -442) {
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
                }


            }
            return false;
        }

        public String getVersionsString(Context c) {
            if (startVersion == Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (endVersion == -1)
                    return null;
                else
                    return c.getString(com.zd.vpn.R.string.version_upto, getAndroidVersionString(c, endVersion));
            }

            if (endVersion == -1)
                return c.getString(com.zd.vpn.R.string.version_and_later, getAndroidVersionString(c, startVersion));


            String startver = getAndroidVersionString(c, startVersion);

            if (endVersion == startVersion)
                return startver;

            return String.format("%s - %s", startver, getAndroidVersionString(c, endVersion));
        }


        private String getAndroidVersionString(Context c, int versionCode) {
            switch (versionCode) {
                case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                    return "4.0 (Ice Cream Sandwich)";
                case -441:
                    return "4.4.1 (KitKat)";
                case -442:
                    return "4.4.2 (KitKat)";
                case Build.VERSION_CODES.JELLY_BEAN_MR2:
                    return "4.3 (Jelly Bean MR2)";
                case Build.VERSION_CODES.KITKAT:
                    return "4.4 (KitKat)";
                case Build.VERSION_CODES.LOLLIPOP:
                    return "5.0 (Lollipop)";
                default:
                    return "API " + versionCode;
            }
        }


    }

    private static FAQEntry[] faqitemsVersionSpecific = {

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.faq_howto_title, com.zd.vpn.R.string.faq_howto),

            new FAQEntry(Build.VERSION_CODES.LOLLIPOP, -1, com.zd.vpn.R.string.samsung_broken_title, com.zd.vpn.R.string.samsung_broken),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.faq_duplicate_notification_title, com.zd.vpn.R.string.faq_duplicate_notification),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.faq_androids_clients_title, com.zd.vpn.R.string.faq_android_clients),


            new FAQEntry(Build.VERSION_CODES.LOLLIPOP, -1, com.zd.vpn.R.string.ab_lollipop_reinstall_title, com.zd.vpn.R.string.ab_lollipop_reinstall),


            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.JELLY_BEAN_MR2, com.zd.vpn.R.string.vpn_tethering_title, com.zd.vpn.R.string.faq_tethering),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.JELLY_BEAN_MR2, com.zd.vpn.R.string.broken_images, com.zd.vpn.R.string.broken_images_faq),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.battery_consumption_title, com.zd.vpn.R.string.baterry_consumption),


            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.KITKAT, com.zd.vpn.R.string.faq_system_dialogs_title, com.zd.vpn.R.string.faq_system_dialogs),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.tap_mode, com.zd.vpn.R.string.faq_tap_mode),

            new FAQEntry(Build.VERSION_CODES.JELLY_BEAN_MR2, Build.VERSION_CODES.JELLY_BEAN_MR2, com.zd.vpn.R.string.ab_secondary_users_title, com.zd.vpn.R.string.ab_secondary_users),
            new FAQEntry(Build.VERSION_CODES.JELLY_BEAN_MR2, -1, com.zd.vpn.R.string.faq_vpndialog43_title, com.zd.vpn.R.string.faq_vpndialog43),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.tls_cipher_alert_title, com.zd.vpn.R.string.tls_cipher_alert),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.faq_security_title, com.zd.vpn.R.string.faq_security),

            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.faq_shortcut, com.zd.vpn.R.string.faq_howto_shortcut),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.tap_mode, com.zd.vpn.R.string.tap_faq2),

            new FAQEntry(Build.VERSION_CODES.KITKAT, -1, com.zd.vpn.R.string.vpn_tethering_title, com.zd.vpn.R.string.ab_tethering_44),
            new FAQEntry(Build.VERSION_CODES.KITKAT, -441, com.zd.vpn.R.string.ab_kitkat_mss_title, com.zd.vpn.R.string.ab_kitkat_mss),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.copying_log_entries, com.zd.vpn.R.string.faq_copying),

            new FAQEntry(Build.VERSION_CODES.KITKAT, -442, com.zd.vpn.R.string.ab_persist_tun_title, com.zd.vpn.R.string.ab_persist_tun),
            new FAQEntry(Build.VERSION_CODES.KITKAT, -1, com.zd.vpn.R.string.faq_routing_title, com.zd.vpn.R.string.faq_routing),
            new FAQEntry(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT, com.zd.vpn.R.string.ab_kitkat_reconnect_title, com.zd.vpn.R.string.ab_kitkat_reconnect),
            new FAQEntry(Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT, com.zd.vpn.R.string.ab_vpn_reachability_44_title,  com.zd.vpn.R.string.ab_vpn_reachability_44),


            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.ab_only_cidr_title, com.zd.vpn.R.string.ab_only_cidr),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.ab_proxy_title, com.zd.vpn.R.string.ab_proxy),

            new FAQEntry(Build.VERSION_CODES.LOLLIPOP, -1, com.zd.vpn.R.string.ab_not_route_to_vpn_title, com.zd.vpn.R.string.ab_not_route_to_vpn),
            new FAQEntry(Build.VERSION_CODES.ICE_CREAM_SANDWICH, -1, com.zd.vpn.R.string.tap_mode, com.zd.vpn.R.string.tap_faq3),

            // DNS weirdness in Samsung 5.0: https://plus.google.com/117315704597472009168/posts/g78bZLWmqgD
    };


    private RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(com.zd.vpn.R.layout.faq, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dpWidth = (int) (displaymetrics.widthPixels / getResources().getDisplayMetrics().density);

        //better way but does not work on 5.0
        //int dpWidth = (int) (container.getWidth()/getResources().getDisplayMetrics().density);
        int columns = dpWidth / 360;
        columns = Math.max(1, columns);


        mRecyclerView = (RecyclerView) v.findViewById(com.zd.vpn.R.id.faq_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(new FaqViewAdapter(getActivity(), getFAQEntries()));

        return v;
    }

    private FAQEntry[] getFAQEntries() {
        Vector<FAQEntry> faqItems = new Vector<>();

        for (FAQEntry fe : faqitemsVersionSpecific) {
            if (fe.runningVersion())
                faqItems.add(fe);
        }
        for (FAQEntry fe : faqitemsVersionSpecific) {
            if (!fe.runningVersion())
                faqItems.add(fe);
        }

        return faqItems.toArray(new FAQEntry[faqItems.size()]);
    }

}
