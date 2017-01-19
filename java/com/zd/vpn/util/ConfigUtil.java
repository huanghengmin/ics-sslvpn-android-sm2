package com.zd.vpn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.zd.vpn.R;
import com.zd.vpn.VpnProfile;
import com.zd.vpn.checker.PropertiesUtils;
import com.zd.vpn.core.ConfigParser.ConfigParseError;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import com.zd.vpn.core.ConfigParser;
import com.zd.vpn.core.ProfileManager;

public class ConfigUtil {
	private VpnProfile mResult;
	private List<String> mPathsegments;
	private String mAliasName=null;
	private String mPossibleName=null;
	private String uuid = null;
	private Context context;
	public ConfigUtil(Context context){
		this.context = context;
	}

	private void doImport(InputStream is) {
		ConfigParser cp = new ConfigParser();
		try {
			InputStreamReader isr = new InputStreamReader(is);

			cp.parseConfig(isr);
			VpnProfile vp = cp.convertProfile();
			mResult = vp;
			embedFiles();
			return;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigParseError e) {
			e.printStackTrace();
		} 
		mResult=null;

	}
	
	void embedFiles() {
		// This where I would like to have a c++ style
		// void embedFile(std::string & option)

		if (mResult.mPKCS12Filename!=null) {
			File pkcs12file = findFileRaw(mResult.mPKCS12Filename);
			if(pkcs12file!=null) {
				mAliasName = pkcs12file.getName().replace(".p12", "");
			} else {
				mAliasName = "Imported PKCS12";
			}
		}
			
		
		mResult.mCaFilename = embedFile(mResult.mCaFilename);
		mResult.mClientCertFilename = embedFile(mResult.mClientCertFilename);
		mResult.mClientEncCertFilename = embedFile(mResult.mClientEncCertFilename);
		mResult.mClientKeyFilename = embedFile(mResult.mClientKeyFilename);
		mResult.mClientEncKeyFilename = embedFile(mResult.mClientEncKeyFilename);
		mResult.mTLSAuthFilename = embedFile(mResult.mTLSAuthFilename);
		mResult.mPKCS12Filename = embedFile(mResult.mPKCS12Filename,true);
		

		if(mResult.mUsername == null && mResult.mPassword != null ){
			String data =embedFile(mResult.mPassword);
			ConfigParser.useEmbbedUserAuth(mResult, data);			
		}
	}
	private String embedFile(String filename) {
		return embedFile(filename, false);		
	}

	private String embedFile(String filename, boolean base64encode)
	{
		if(filename==null)
			return null;

		// Already embedded, nothing to do
		if(filename.startsWith(VpnProfile.INLINE_TAG))
			return filename;

		File possibleFile = findFile(filename);
		if(possibleFile==null)
			return filename;
		else
			return readFileContent(possibleFile,base64encode);

	}
	private File findFile(String filename) {
		File foundfile =findFileRaw(filename);

		return foundfile;
	}
	String readFileContent(File possibleFile, boolean base64encode) {
		byte [] filedata;
		try {
			filedata = readBytesFromFile(possibleFile);
		} catch (IOException e) {
			return null;
		}
		
		String data;
		if(base64encode) {
			data = Base64.encodeToString(filedata, Base64.DEFAULT);
		} else {
			data = new String(filedata);

		}
		return VpnProfile.INLINE_TAG + data;
		
	}
	private byte[] readBytesFromFile(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		long len= file.length();


		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) len];

		// Read in the bytes
		int offset = 0;
		int bytesRead = 0;
		while (offset < bytes.length
				&& (bytesRead=input.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += bytesRead;
		}

		input.close();
		return bytes;
	}


	private File findFileRaw(String filename)
	{
		if(filename == null || filename.equals(""))
			return null;

		// Try diffent path relative to /mnt/sdcard
		File sdcard = Environment.getExternalStorageDirectory();
		File root = new File("/");

		Vector<File> dirlist = new Vector<File>();

		for(int i=mPathsegments.size()-1;i >=0 ;i--){
			String path = "";
			for (int j = 0;j<=i;j++) {
				path += "/" + mPathsegments.get(j);
			}
			dirlist.add(new File(path));
		}
		dirlist.add(sdcard);
		dirlist.add(root);


		String[] fileparts = filename.split("/");
		for(File rootdir:dirlist){
			String suffix="";
			for(int i=fileparts.length-1; i >=0;i--) {
				if(i==fileparts.length-1)
					suffix = fileparts[i];
				else
					suffix = fileparts[i] + "/" + suffix;

				File possibleFile = new File(rootdir,suffix);
				if(!possibleFile.canRead())
					continue;
				return possibleFile;

			}
		}
		return null;
	}
	public String config(Context paramContext){
		Uri uri = new Uri.Builder().path(paramContext.getFilesDir().getAbsolutePath()+ PropertiesUtils.CONFIG_PATH).scheme("file").build();
		mPossibleName = uri.getLastPathSegment();
		if(mPossibleName!=null){
			mPossibleName =mPossibleName.replace(".ovpn", "");
			mPossibleName =mPossibleName.replace(".conf", "");
		}
		mPathsegments = uri.getPathSegments();
		try {
			InputStream is = context.getContentResolver().openInputStream(uri);
			doImport(is);
			saveProfile(context);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		uuid = mResult.getUUIDString();
		context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE).edit().putString("vpn.uuid", uuid).commit();
		return uuid;
	}
	
	private void saveProfile(Context context) {
		ProfileManager vpl = ProfileManager.getInstance(context);
        Collection<VpnProfile> vpnProfiles =  vpl.getProfiles();
        if(vpnProfiles!=null&&vpnProfiles.size()>0){
            for (VpnProfile p:vpnProfiles){
                vpl.removeProfile(context,p);
            }
        }
		setUniqueProfileName(vpl,context);
		vpl.addProfile(mResult);
		vpl.saveProfile(context, mResult);
		vpl.saveProfileList(context);
	}
	private void setUniqueProfileName(ProfileManager vpl,Context context) {
		int i=0;

		String newname = mPossibleName;

		while(vpl.getProfileByName(newname)!=null) {
			i++;
			if(i==1)
				newname = context.getString(R.string.converted_profile);
			else
				newname = context.getString(R.string.converted_profile_i,i);
		}

		mResult.mName=newname;
	}

	public String getUuid() {
		String uuid = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE).getString("vpn.uuid", null);
		return uuid;
	}
}
