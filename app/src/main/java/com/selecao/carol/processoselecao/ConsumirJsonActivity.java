package com.selecao.carol.processoselecao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ConsumirJsonActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new DownloadJsonAsyncTask()
				.execute("https://mystique-v1-submarino.b2w.io/mystique/search?content=smart%20tv%2032&sortBy=moreRelevant&source=neemu");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Televisor televisor = (Televisor) l.getAdapter().getItem(position);

		Intent intent = new Intent(this, InformacoesActivity.class);
		intent.putExtra("televisor", televisor);
		startActivity(intent);
	}

	class DownloadJsonAsyncTask extends AsyncTask<String, Void, List<Televisor>> {
		ProgressDialog dialog;

		//Exibe pop-up indicando que est� sendo feito o download do JSON
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ConsumirJsonActivity.this, "Aguarde",
					"Fazendo download do JSON");
		}

		//Acessa o servi�o do JSON e retorna a lista de Televisors
		@Override
		protected List<Televisor> doInBackground(String... params) {
			String urlString = params[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(urlString);
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					String json = getStringFromInputStream(instream);
					instream.close();
					List<Televisor> Televisors = getTelevisors(json);
					return Televisors;
				}
			} catch (Exception e) {
				Log.e("Erro", "Falha ao acessar Web service", e);
			}
			return null;
		}


		//Depois de executada a chamada do servi�o 
		@Override
		protected void onPostExecute(List<Televisor> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result.size() > 0) {
				ArrayAdapter<Televisor> adapter = new ArrayAdapter<Televisor>(
						ConsumirJsonActivity.this,
						android.R.layout.simple_list_item_1, result);
				setListAdapter(adapter);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ConsumirJsonActivity.this)
						.setTitle("Erro")
						.setMessage("N�o foi poss�vel acessar as informa��es!!")
						.setPositiveButton("OK", null);
				builder.create().show();
			}
		}
		
		//Retorna uma lista de Televisors com as informa��es retornadas do JSON
		private List<Televisor> getTelevisors(String jsonString) {
			List<Televisor> Televisors = new ArrayList<Televisor>();
			try {
				JSONArray TelevisorsJson = new JSONArray(jsonString);
				JSONObject Televisor;

				for (int i = 0; i < TelevisorsJson.length(); i++) {
					Televisor = new JSONObject(TelevisorsJson.getString(i));
					Log.i("Televisor ENCONTRADA: ",
							"nome=" + Televisor.getString("nome"));

					Televisor objetoTelevisor = new Televisor();
					objetoTelevisor.setDescricao(Televisor.getString("Descricao"));
					objetoTelevisor.setValorTV(Televisor.getString("Valor da TV"));
					televisor.add(objetoTelevisor);
				}

			} catch (JSONException e) {
				Log.e("Erro", "Erro no parsing do JSON", e);
			}
			return Televisors;
		}
		

		//Converte objeto InputStream para String
		private String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();

		}

	}
}
