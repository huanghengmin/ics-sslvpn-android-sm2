/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.zd.vpn.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zd.vpn.core.VpnStatus;
import com.zd.vpn.core.X509Utils;

import java.io.IOException;

import com.zd.vpn.VpnProfile;
import com.zd.vpn.activities.FileSelect;
import com.zd.vpn.fragments.Utils;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;


public class FileSelectLayout extends LinearLayout implements OnClickListener {


    public void parseResponse(Intent data, Context c) {

            try {
                String newData = Utils.getFilePickerResult(fileType, data, c);
                if (newData!=null)
                    setData(newData, c);

                if (newData == null) {
                    String fileData = data.getStringExtra(FileSelect.RESULT_DATA);
                    setData(fileData, c);
                }

            } catch (IOException | SecurityException e) {
                VpnStatus.logException(e);
            }
    }

    public interface FileSelectCallback {

        String getString(int res);

        void startActivityForResult(Intent startFC, int mTaskId);
    }

    private boolean mIsCertificate;
    private TextView mDataView;
    private String mData;
    private FileSelectCallback mFragment;
    private int mTaskId;
    private Button mSelectButton;
    private Utils.FileType fileType;
    private String mTitle;
    private boolean mShowClear;
    private TextView mDataDetails;
    private Button mShowClearButton;


    public FileSelectLayout(Context context, AttributeSet attrset) {
        super(context, attrset);

        TypedArray ta = context.obtainStyledAttributes(attrset, com.zd.vpn.R.styleable.FileSelectLayout);

        setupViews(ta.getString(com.zd.vpn.R.styleable.FileSelectLayout_fileTitle), ta.getBoolean(com.zd.vpn.R.styleable.FileSelectLayout_certificate, true)
        );

        ta.recycle();
    }

    public FileSelectLayout (Context context, String title, boolean isCertificate, boolean showClear)
    {
        super(context);

        setupViews(title, isCertificate);
        mShowClear = showClear;

    }

    private void setupViews(String title, boolean isCertificate) {
        inflate(getContext(), com.zd.vpn.R.layout.file_select, this);

        mTitle = title;
        mIsCertificate = isCertificate;

        TextView tView = (TextView) findViewById(com.zd.vpn.R.id.file_title);
        tView.setText(mTitle);

        mDataView = (TextView) findViewById(com.zd.vpn.R.id.file_selected_item);
        mDataDetails = (TextView) findViewById(com.zd.vpn.R.id.file_selected_description);
        mSelectButton = (Button) findViewById(com.zd.vpn.R.id.file_select_button);
        mSelectButton.setOnClickListener(this);

        mShowClearButton = (Button) findViewById(com.zd.vpn.R.id.file_clear_button);
        mShowClearButton.setOnClickListener(this);
    }

    public void setClearable(boolean clearable)
    {
        mShowClear = clearable;
        if (mShowClearButton != null && mData !=null)
            mShowClearButton.setVisibility(mShowClear? VISIBLE : GONE);

    }


    public void setCaller(FileSelectCallback fragment, int i, Utils.FileType ft) {
        mTaskId = i;
        mFragment = fragment;
        fileType = ft;
    }

    public void getCertificateFileDialog() {
        Intent startFC = new Intent(getContext(), FileSelect.class);
        startFC.putExtra(FileSelect.START_DATA, mData);
        startFC.putExtra(FileSelect.WINDOW_TITLE, mTitle);
        if (fileType == Utils.FileType.PKCS12)
            startFC.putExtra(FileSelect.DO_BASE64_ENCODE, true);
        if (mShowClear)
            startFC.putExtra(FileSelect.SHOW_CLEAR_BUTTON, true);

        mFragment.startActivityForResult(startFC, mTaskId);
    }


    public String getData() {
        return mData;
    }

    public void setData(String data, Context c) {
        mData = data;
        if (data == null) {
            mDataView.setText(c.getString(com.zd.vpn.R.string.no_data));
            mDataDetails.setText("");
            mShowClearButton.setVisibility(GONE);
        } else {
            if (mData.startsWith(VpnProfile.DISPLAYNAME_TAG)) {
                mDataView.setText(c.getString(com.zd.vpn.R.string.imported_from_file, VpnProfile.getDisplayName(mData)));
            } else if (mData.startsWith(VpnProfile.INLINE_TAG))
                mDataView.setText(com.zd.vpn.R.string.inline_file_data);
            else
                mDataView.setText(data);
            if (mIsCertificate) {
                mDataDetails.setText(X509Utils.getCertificateFriendlyName(c, data));
            }

            // Show clear button if it should be shown
            mShowClearButton.setVisibility(mShowClear? VISIBLE : GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mSelectButton) {
            Intent startFilePicker=null;
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                startFilePicker = Utils.getFilePickerIntent(getContext(), fileType);
            }

            if (startFilePicker != null) {
                mFragment.startActivityForResult(startFilePicker, mTaskId);
            } else {
                getCertificateFileDialog();
            }
        } else if (v == mShowClearButton) {
            setData(null, getContext());
        }
    }




    public void setShowClear() {
        mShowClear = true;
    }

}
