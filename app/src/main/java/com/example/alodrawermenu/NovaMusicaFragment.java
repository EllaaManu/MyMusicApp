package com.example.alodrawermenu;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.alodrawermenu.MainActivity;
import com.example.alodrawermenu.R;
import com.example.alodrawermenu.db.bean.Genero;
import com.example.alodrawermenu.db.bean.Musica;
import com.example.alodrawermenu.db.dal.GeneroDAL;
import com.example.alodrawermenu.db.dal.MusicaDAL;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;


// Suas variáveis de classe
public class NovaMusicaFragment extends Fragment {
    // ... (outras variáveis)
    private int idGenero;

    // Views para o cenário fixo
    private TextInputLayout layoutGeneroFixo;
    private TextInputEditText etGeneroFixo;

    // Views para o cenário de seleção
    private TextInputLayout layoutGeneroSpinner;
    private AutoCompleteTextView spinnerGenero;

    // Views para os outros campos
    private TextInputLayout tilTitulo;
    private TextInputLayout tilInterprete;
    private TextInputLayout tilAno;
    private TextInputLayout tilDuracao;

    private Button btnSalvar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NovaMusicaFragment() {
    }

    public static NovaMusicaFragment newInstance(String param1, String param2) {
        NovaMusicaFragment fragment = new NovaMusicaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NovaMusicaFragment newInstance(int generoId) {
            NovaMusicaFragment fragment = new NovaMusicaFragment();
            Bundle args = new Bundle();
            args.putInt("genero_id", generoId);
            fragment.setArguments(args);
        return fragment;

    }

    public static NovaMusicaFragment newInstance(Musica musica) {
        NovaMusicaFragment fragment = new NovaMusicaFragment();
        Bundle args = new Bundle();
        args.putInt("musica_id", musica.getId());
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_nova_musica, container, false);

        // Inicialização de todas as views com os tipos CORRETOS
        layoutGeneroFixo = view.findViewById(R.id.layoutGeneroFixo);
        etGeneroFixo = view.findViewById(R.id.etGeneroFixo);
        layoutGeneroSpinner = view.findViewById(R.id.layoutGeneroSpinner);
        spinnerGenero = view.findViewById(R.id.spinnerGenero);
        tilTitulo = view.findViewById(R.id.tilTitulo);
        tilInterprete = view.findViewById(R.id.tilInterprete);
        tilAno = view.findViewById(R.id.tilAno);
        tilDuracao = view.findViewById(R.id.tilDuracao);
        btnSalvar = view.findViewById(R.id.btnSalvar);

        // Lógica de verificação do argumento e configuração dos layouts
        if (getArguments() != null && getArguments().containsKey("genero_id")) {
            layoutGeneroSpinner.setVisibility(View.GONE);
            layoutGeneroFixo.setVisibility(View.VISIBLE);

            idGenero = getArguments().getInt("genero_id");
            GeneroDAL dal = new GeneroDAL(getContext());
            Genero genero = dal.get(idGenero);
            if (genero != null) {
                etGeneroFixo.setText(genero.getNome());
            }
        } else {
            // Caso 2: O fragmento foi acessado para cadastro ou alteração
            layoutGeneroFixo.setVisibility(View.GONE);
            layoutGeneroSpinner.setVisibility(View.VISIBLE);

            // Ações para o dropdown:
            spinnerGenero.setInputType(InputType.TYPE_NULL);
            spinnerGenero.setKeyListener(null);
            spinnerGenero.setOnClickListener(v -> spinnerGenero.showDropDown());

            // Carrega a lista de todos os gêneros do banco de dados
            GeneroDAL dal = new GeneroDAL(getContext());
            List<Genero> generos = dal.get("");
            List<String> ng = new ArrayList<>();

            if (!generos.isEmpty()) {
                List<String> nomesGeneros = new ArrayList<>();
                for (Genero genero : generos) {
                    nomesGeneros.add(genero.getNome());
                }
                ng = nomesGeneros;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, nomesGeneros);
                spinnerGenero.setAdapter(adapter);

                spinnerGenero.setOnItemClickListener((parent, v, position, id) -> {
                    Genero generoSelecionado = generos.get(position);
                    this.idGenero = generoSelecionado.getId();
                });
            } else {
                Toast.makeText(getContext(), "Nenhum gênero disponível.", Toast.LENGTH_LONG).show();
                spinnerGenero.setEnabled(false);
            }

            // Lógica para pré-preencher campos em caso de alteração
            if (getArguments().containsKey("musica_id")) {
                int musicaId = getArguments().getInt("musica_id");
                MusicaDAL musicaDAL = new MusicaDAL(getContext());
                Musica musica = musicaDAL.get(musicaId);

                // Preenche os campos de texto com os dados da música
                tilTitulo.getEditText().setText(musica.getTitulo());
                tilInterprete.getEditText().setText(musica.getInterprete());
                tilAno.getEditText().setText(String.valueOf(musica.getAno()));
                tilDuracao.getEditText().setText(String.valueOf(musica.getDuracao()));

                // Pré-seleciona o gênero no dropdown
                if (musica.getGenero() != null) {
                    // 1. Encontre a posição do gênero da música na lista de todos os gêneros
                    int posicao = -1;
                    for (int i = 0; i < generos.size(); i++) {
                        if (generos.get(i).getId() == musica.getGenero().getId()) {
                            posicao = i;
                            break;
                        }
                    }

                    // 2. Se a posição for válida, defina o texto e o ID do gênero
                    if (posicao != -1) {
                        spinnerGenero.setText(ng.get(posicao), false);
                        this.idGenero = musica.getGenero().getId();
                    }
                }
            }
        }

        // Ação do botão "Salvar" (usa o 'idGenero' da classe)
        if (getArguments().containsKey("musica_id")) {
            btnSalvar.setText("Alterar");
            btnSalvar.setOnClickListener(v -> {
                // Obtenha o ID da música a ser alterada
                int musicaId = getArguments().getInt("musica_id");

                String titulo = tilTitulo.getEditText().getText().toString().trim();
                String interprete = tilInterprete.getEditText().getText().toString().trim();
                String anoStr = tilAno.getEditText().getText().toString().trim();
                String duracaoStr = tilDuracao.getEditText().getText().toString().trim();

                // Crie o objeto Musica e defina o ID
                Musica musicaAlterada = new Musica();
                musicaAlterada.setId(musicaId); // <-- **Esta é a linha crucial!**
                musicaAlterada.setTitulo(titulo);
                musicaAlterada.setInterprete(interprete);
                musicaAlterada.setAno(Integer.parseInt(anoStr));
                musicaAlterada.setDuracao(Double.parseDouble(duracaoStr));

                // Verifique se o idGenero é válido antes de buscar
                if (idGenero > 0) {
                    GeneroDAL g = new GeneroDAL(getContext());
                    musicaAlterada.setGenero(g.get(idGenero));
                } else {
                    // Caso o gênero não tenha sido alterado, recupere-o do objeto original
                    MusicaDAL musicaDAL = new MusicaDAL(requireContext());
                    Musica original = musicaDAL.get(musicaId);
                    if (original != null) {
                        musicaAlterada.setGenero(original.getGenero());
                    }
                }

                MusicaDAL musicaDAL = new MusicaDAL(requireContext());
                boolean resultado = musicaDAL.alterar(musicaAlterada);

                if (resultado) {
                    Toast.makeText(getContext(), "Música alterada com sucesso!", Toast.LENGTH_SHORT).show();
                    // limpar os campos
                    tilTitulo.getEditText().setText("");
                    tilInterprete.getEditText().setText("");
                    tilAno.getEditText().setText("");
                    tilDuracao.getEditText().setText("");
                    spinnerGenero.setText("", false);

                } else {
                    Toast.makeText(getContext(), "Erro ao alterar música", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnSalvar.setOnClickListener(v -> {
                // Acesso aos campos de texto através do TextInputLayout
                String titulo = tilTitulo.getEditText().getText().toString().trim();
                String interprete = tilInterprete.getEditText().getText().toString().trim();
                String anoStr = tilAno.getEditText().getText().toString().trim();
                String duracaoStr = tilDuracao.getEditText().getText().toString().trim();

                if (titulo.isEmpty()) {
                    tilTitulo.setError("O título é obrigatório");
                    return;
                }
                if (idGenero == 0) {
                    // Em caso de erro, você pode exibir uma mensagem ou usar a funcionalidade de erro do TextInputLayout
                    Toast.makeText(getContext(), "Selecione um gênero válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Musica novaMusica = new Musica();
                novaMusica.setTitulo(titulo);
                novaMusica.setInterprete(interprete);
                novaMusica.setAno(Integer.parseInt(anoStr));
                novaMusica.setDuracao(Double.parseDouble(duracaoStr));

                GeneroDAL g = new GeneroDAL(getContext());
                novaMusica.setGenero(g.get(idGenero));

                MusicaDAL musicaDAL = new MusicaDAL(requireContext());
                boolean resultado = musicaDAL.salvar(novaMusica);

                if (resultado) {
                    Toast.makeText(getContext(), "Música cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    // limpar campos
                    tilTitulo.getEditText().setText("");
                    tilInterprete.getEditText().setText("");
                    tilAno.getEditText().setText("");
                    tilDuracao.getEditText().setText("");
                    spinnerGenero.setText("", false);

                } else {
                    Toast.makeText(getContext(), "Erro ao cadastrar música", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}