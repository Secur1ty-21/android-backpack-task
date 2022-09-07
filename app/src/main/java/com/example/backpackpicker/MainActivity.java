package com.example.backpackpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.example.backpackpicker.databinding.ActivityMainBinding;
import com.example.backpackpicker.model.Backpack;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Backpack backpack1;
    private Backpack backpack2;
    private boolean nextBackpack = true;
    private int numOfItems = 1;
    private String listItemsBackpack1 = "";
    private String listItemsBackpack2 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.textBackpack.setText(getString(R.string.helper_for_backpack_input_values, 1));
        binding.buttonTakeInput.setOnClickListener(view -> setBackpackSize());
    }

    private void setBackpackSize() {
        if (nextBackpack) {
            if (checkInput()) {
                hideKeyboard();
                int weight = Integer.parseInt(binding.inputWeight.getText().toString());
                int volume = Integer.parseInt(binding.inputVolume.getText().toString());
                clearInput();
                if (weight == 0 || volume == 0) {
                    return;
                }
                backpack1 = new Backpack(weight, volume);
                binding.textBackpack.setText(getString(R.string.helper_for_backpack_input_values, 2));
                nextBackpack = false;
                showSnackBar(getString(R.string.text_successfully_add_backpack));
                binding.icBackpack1.setVisibility(View.VISIBLE);
                binding.progressBackpack1.setVisibility(View.VISIBLE);
            }
        } else if (checkInput()) {
            hideKeyboard();
            int weight = Integer.parseInt(binding.inputWeight.getText().toString());
            int volume = Integer.parseInt(binding.inputVolume.getText().toString());
            clearInput();
            if (weight == 0 || volume == 0) {
                return;
            }
            backpack2 = new Backpack(weight, volume);
            setUiOnAddsItem();
            showSnackBar(getString(R.string.text_successfully_add_backpack));
        }
    }

    private void clearInput() {
        binding.inputVolume.setText("");
        binding.inputWeight.setText("");
        binding.inputName.setText("");
    }

    private boolean checkInput() {
        return !TextUtils.isEmpty(binding.inputWeight.getText()) && !TextUtils.isEmpty(binding.inputVolume.getText());
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void showSnackBar(String text) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show();
    }

    private void setUiOnAddsItem() {
        binding.textBackpack.setText(getString(R.string.helper_for_item_input_values, numOfItems++));
        binding.buttonTakeInput.setText(getString(R.string.text_button_add));
        binding.icBackpack2.setVisibility(View.VISIBLE);
        binding.progressBackpack2.setVisibility(View.VISIBLE);
        binding.layoutInputName.setVisibility(View.VISIBLE);
        binding.listItemsBackpack1.setVisibility(View.VISIBLE);
        binding.listItemsBackpack2.setVisibility(View.VISIBLE);
        binding.buttonTakeInput.setOnClickListener(view -> addItems());
    }

    private void addItems() {
        if (checkInput() && !TextUtils.isEmpty(binding.inputName.getText())) {
            String name = binding.inputName.getText().toString();
            hideKeyboard();
            int weight = Integer.parseInt(binding.inputWeight.getText().toString());
            int volume = Integer.parseInt(binding.inputVolume.getText().toString());
            listItemsBackpack1 += String.format(getString(R.string.text_item), name, weight, volume);
            clearInput();
            binding.listItemsBackpack1.setText(listItemsBackpack1);
        }
    }
}