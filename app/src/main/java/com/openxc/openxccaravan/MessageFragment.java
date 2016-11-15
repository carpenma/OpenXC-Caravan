package com.openxc.openxccaravan;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.openxcplatform.openxccaravan.R;

public class MessageFragment extends Fragment {
    OnMessageSendListener mCallback;

    public interface OnMessageSendListener {
        public void sendMessage(String msg);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Messaging");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMessageSendListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMessageSendListener");
        }
    }
    public void send_butonClick (View view) {
        EditText sendText = (EditText) getView().findViewById(R.id.msgBody);
        mCallback.sendMessage(sendText.getText().toString());
    }

    public void addMessage(String msg) {
        
    }
}
