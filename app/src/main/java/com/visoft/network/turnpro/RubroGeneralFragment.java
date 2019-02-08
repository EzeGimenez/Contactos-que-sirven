package com.visoft.network.turnpro;

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
import android.widget.ListView;
import android.widget.TextView;

import com.visoft.network.R;

public class RubroGeneralFragment extends Fragment {

    private ConfiguratorRubro r;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rubro_especifico_fragment, container, false);
    }

    public void setParent(ConfiguratorRubro r) {
        this.r = r;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.selecciona_area_trabajo);

        //Componentes gráficas
        ListView listView = view.findViewById(R.id.listView);

        final String[] rubrosAID = getResources().getStringArray(R.array.rubrosGeneralesID);
        listView.setAdapter(new RubrosGeneralesAdapter(getContext(), rubrosAID));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("rubro", rubrosAID[position]);
                r.avanzar(bundle);
            }
        });
    }

    private class RubrosGeneralesAdapter extends ArrayAdapter<String> {
        private String[] rubrosArray;

        RubrosGeneralesAdapter(Context context, String[] rubros) {
            super(context, R.layout.rubros_generales_row, rubros);
            this.rubrosArray = rubros;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.rubros_generales_row, parent, false);
            }
            TextView tvRubro = convertView.findViewById(R.id.tvRubro);
            int id = getResources().getIdentifier(rubrosArray[position],
                    "string",
                    getActivity().getPackageName());
            tvRubro.setText(getString(id));
            return convertView;
        }
    }
}
