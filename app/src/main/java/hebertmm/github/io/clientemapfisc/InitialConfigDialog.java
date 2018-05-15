package hebertmm.github.io.clientemapfisc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InitialConfigDialog extends DialogFragment implements DialogInterface.OnClickListener{
    NetworkService networkService;
    EditText txtNumber;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.init_dialog, null);
        txtNumber = v.findViewById(R.id.telNumber);
        builder.setMessage("O número do telefone não foi configurado, ou está configurado incorretamente. Digite abaixo:")
                .setPositiveButton("Confirmar", this)
                .setNegativeButton("Cancelar", this);
        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE){
            NetworkService.startActionGetId(getActivity(),
                    txtNumber.getText().toString());

        }

    }
}
