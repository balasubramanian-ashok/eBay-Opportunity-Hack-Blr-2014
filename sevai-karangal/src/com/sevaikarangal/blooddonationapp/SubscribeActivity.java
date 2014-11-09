package com.sevaikarangal.blooddonationapp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sevaikarangal.blooddonationapp.bean.DonorRequest;

public class SubscribeActivity extends Activity {

	Spinner bloodGrp;
	Spinner gender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscribe);

		addItemsOnSpinners();

		// donorid 5639445604728832
		Button submit = (Button) findViewById(R.id.button1);
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText personName = (EditText) findViewById(R.id.personName);
				EditText contactNum = (EditText) findViewById(R.id.contactn);
				EditText locality = (EditText) findViewById(R.id.locality);
				// EditText age = (EditText) findViewById(R.id.age);
				EditText height = (EditText) findViewById(R.id.height);
				EditText weight = (EditText) findViewById(R.id.weight);
				EditText city = (EditText) findViewById(R.id.city);

				final DonorRequest rq = new DonorRequest();
				rq.setBloodGroup(bloodGrp.getSelectedItem().toString());
				rq.setGender(gender.getSelectedItem().toString());
				rq.setName(personName.getText().toString());
				if (contactNum.getText() != null)
					rq.setPhoneNumber(Long.parseLong(contactNum.getText()
							.toString()));
				rq.setLocality(locality.getText().toString());
				rq.setLastDonatedDate(null);
				rq.setCity(city.getText().toString());
				rq.setHeight(Double.parseDouble(height.getText().toString()));
				rq.setWeight(Double.parseDouble(weight.getText().toString()));

				AsyncHttpClient client = new AsyncHttpClient();

				StringEntity entity = null;
				try {
					entity = new StringEntity(rq.toString());
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), rq.toString(),
						Toast.LENGTH_LONG).show();

				client.post(
						getApplicationContext(),
						"http://1-dot-blood-donor-svc.appspot.com/datastore/donor",
						entity, "application/json",
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] response) {
								Toast.makeText(getApplicationContext(),
										R.string.subSuccess, Toast.LENGTH_LONG)
										.show();
								
								String donorId = (new String(response));
								Intent requestListActivityIntent = new Intent(
										SubscribeActivity.this,
										RequestListActivity.class);

								
								Intent notifyServiceIntent = new Intent(SubscribeActivity.this,
										NotifyService.class);
								notifyServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								notifyServiceIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
								notifyServiceIntent.putExtra("bloodGroup", rq.getBloodGroup());
								notifyServiceIntent.putExtra("city", rq.getCity());
								notifyServiceIntent.putExtra("locality", rq.getLocality());
								startService(notifyServiceIntent);
								
								
								//Add the Donor details to shared preference
								Editor edit = ((DonorApplication)getApplication()).getPref().edit();
								edit.putString("DonorId", donorId);
								edit.putString("City", rq.getCity());
								edit.putString("BloodGroup", rq.getBloodGroup());
								edit.putString("Locality", rq.getLocality());
								edit.commit();
								
								
								requestListActivityIntent.putExtra("bloodGroup", rq.getBloodGroup());
								requestListActivityIntent.putExtra("locality", rq.getLocality());
								requestListActivityIntent.putExtra("city", rq.getCity());
								startActivity(requestListActivityIntent);
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] errorResponse,
									Throwable e) {
								Toast.makeText(getApplicationContext(),
										R.string.subFailed, Toast.LENGTH_LONG)
										.show();
							}
						});
			}
		});

//		SharedPreferences pref = ((DonorApplication) getApplication())
//				.getPref();
//		//Back upplan for demo
//		String donorId = pref.getString("DonorId", "5639445604728832");
//		
//		AsyncHttpClient client = new AsyncHttpClient();
//
//		client.get(
//				getApplicationContext(),
//				"http://1-dot-blood-donor-svc.appspot.com/datastore/donor/"+donorId,
//				new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							byte[] response) {
//						
//						Gson gson = new Gson();
//						DonorRequest donorDetail = gson.fromJson(new String(response), DonorRequest.class);
//						
//						
//						Intent intent = new Intent(SubscribeActivity.this,
//								NotifyService.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//						intent.putExtra("bloodGroup", donorDetail.getBloodGroup());
//						intent.putExtra("city", donorDetail.getCity());
//						intent.putExtra("locality", donorDetail.getLocality());
//						startService(intent);
//
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							byte[] errorResponse, Throwable e) {
//						Toast.makeText(getApplicationContext(),
//								new String(errorResponse), Toast.LENGTH_LONG)
//								.show();
//					}
//				});

	}

	public void addItemsOnSpinners() {

		bloodGrp = (Spinner) findViewById(R.id.bloodGrp);
		List<String> list = new ArrayList<String>();
		list.add("O+ve");
		list.add("O-ve");
		list.add("A+ve");
		list.add("A-ve");
		list.add("B+ve");
		list.add("B-ve");
		list.add("AB+ve");
		list.add("AB-ve");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bloodGrp.setAdapter(dataAdapter);

		
		gender = (Spinner) findViewById(R.id.gender);
		list = new ArrayList<String>();
		list.add("Male");
		list.add("Female");
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gender.setAdapter(dataAdapter2);
	}

}
