package com.selecao.carol.processoselecao;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.selecao.carol.processoselecao.Televisor


public class InformacoesActivity extends Activity {

	private TextView txtDescricao;
	private TextView txtValorTV;
	private TextView txtUrlImagem;
	private Televisor televisor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacoes);

		televisor = (Televisor) getIntent().getSerializableExtra("televisor" + "");
		
		txtDescricao = (TextView) findViewById(R.id.txtDescricao);
		txtValorTV = (TextView) findViewById(R.id.txtValorTV);
		txtUrlImagem = (TextView) findViewById(R.id.txtUrlImagem);
		
		txtDescricao.setText(televisor.getDescricao());
		txtValorTV.setText(televisor.getValorTV());
		txtUrlImagem.setText(televisor.getUrlImagem());

	}

}
