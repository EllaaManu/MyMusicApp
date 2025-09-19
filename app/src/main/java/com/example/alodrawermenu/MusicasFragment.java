
package com.example.alodrawermenu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.alodrawermenu.db.bean.Musica;
import com.example.alodrawermenu.db.dal.MusicaDAL;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MusicasFragment extends Fragment {
    private ListView lvMusicas;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainActivity mainActivity;

    public MusicasFragment() {
        // Required empty public constructor
    }
    public static MusicasFragment newInstance(String param1, String param2) {
        MusicasFragment fragment = new MusicasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity=(MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_musicas, container, false);
        lvMusicas=view.findViewById(R.id.lvMusicas);
        // O restante do seu código antes do Listener...

        lvMusicas.setOnItemLongClickListener((adapterView, view1, i, l) -> {
            Musica musica = (Musica) adapterView.getItemAtPosition(i);

            new AlertDialog.Builder(adapterView.getContext())
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir a música \"" + musica.getTitulo() + "\"?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        MusicaDAL dal = new MusicaDAL(adapterView.getContext());
                        dal.apagar(musica.getId());

                        // Use a view principal do Fragment (a "view" que você inflou) para o Snackbar
                        Snackbar.make(view, "Música excluída com sucesso!", Snackbar.LENGTH_SHORT).show();

                        carregarMusicas(view);
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
        lvMusicas.setOnItemClickListener((adapterView, view1, i, l) -> {
            Musica musica =(Musica)adapterView.getItemAtPosition(i);
            mainActivity.alterarMusica(musica);

        });
        carregarMusicas(view);
        return view;
    }

    private void carregarMusicas(View view) {
        MusicaDAL dal =new MusicaDAL(view.getContext());
        List<Musica> musicaList=dal.get("");
        lvMusicas.setAdapter(new ArrayAdapter<Musica>(view.getContext(),
                android.R.layout.simple_list_item_1,musicaList));
    }
}
