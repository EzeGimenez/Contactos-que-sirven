package com.visoft.jobfinder.turnprofragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.visoft.jobfinder.R;
import com.visoft.jobfinder.TurnProActivity;

public class RubroEspecificoFragment extends Fragment {

    //Componentes gráficas
    private ListView listView;
    private String rubroGeneral;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rubro_especifico_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText("Selecciona una especificación");

        rubroGeneral = getArguments().getString("RubroGeneral", null);

        int id = getResources().getIdentifier(
                rubroGeneral.toLowerCase().replaceAll(" ", ""),
                "array",
                getActivity().getPackageName());
        final String[] rubroEspecificos = getResources().getStringArray(id);

        listView = view.findViewById(R.id.listView);
        listView.setAdapter(new RubrosGeneralesAdapter(getContext(), rubroEspecificos));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("rubroEspecifico", rubroEspecificos[position]);
                ((TurnProActivity) getActivity()).onForthPressed(bundle);
            }
        });

        Button btnPrev = getActivity().findViewById(R.id.btnPrev);
        btnPrev.setEnabled(true);
    }

    private class RubrosGeneralesAdapter extends ArrayAdapter<String> {
        private String[] rubrosArray;
        private Context context;

        public RubrosGeneralesAdapter(Context context, String[] rubros) {
            super(context, R.layout.rubros_especificos_row, rubros);
            this.rubrosArray = rubros;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.rubros_especificos_row, parent, false);
            TextView tvRubro = view.findViewById(R.id.tvRubro);
            tvRubro.setText(rubrosArray[position]);
            return view;
        }
    }
}
