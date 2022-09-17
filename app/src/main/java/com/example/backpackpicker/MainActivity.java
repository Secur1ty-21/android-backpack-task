package com.example.backpackpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.backpackpicker.databinding.ActivityMainBinding;
import com.example.backpackpicker.model.Backpack;
import com.example.backpackpicker.model.Item;
import com.example.backpackpicker.utility.WeightCalculator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Backpack backpack1;
    private Backpack backpack2;
    private boolean firstBackpack = true;
    private int numOfItems = 1;
    private boolean itemState = false;
    private WeightCalculator weightCalculator;
    private SharedPreferences preferences;
    private final Gson gson = new Gson();
    // Ids for sharedPreference
    private final String saveNumOfItems = "numOfItems";
    private final String saveBackpack1 = "backpack1";
    private final String saveBackpack2 = "backpack2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getPreferences(MODE_PRIVATE);
        recoverState();
        if (firstBackpack) {
            binding.textBackpack.setText(getString(R.string.helper_for_backpack_input_values, 1));
        }
        if (!itemState) {
            binding.buttonTakeInput.setOnClickListener(view -> setBackpackSize());
        }
        binding.buttonReset.setOnClickListener(view -> resetUi());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Сохраняем текущее состояние приложения.
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(saveNumOfItems, numOfItems);
        editor.putString(saveBackpack1, gson.toJson(backpack1));
        editor.putString(saveBackpack2, gson.toJson(backpack2));
        editor.apply();
    }

    /**
     * Шаг 1 - Получение размеров рюкзаков.
     */
    private void setBackpackSize() {
        if (checkInput()) {
            hideKeyboard();
            //noinspection ConstantConditions проверяется на null в checkInput()
            final float weight = Float.parseFloat(binding.inputWeight.getText().toString());
            //noinspection ConstantConditions проверяется на null в checkInput()
            final float volume = Float.parseFloat(binding.inputVolume.getText().toString());
            clearInput();
            if (weight == 0 || volume == 0) {
                showSnackBar(getString(R.string.text_error_add_backpack));
                return;
            }
            showSnackBar(getString(R.string.text_successfully_add_backpack));
            if (firstBackpack) {
                backpack1 = new Backpack(weight, volume);
                showFirstBackpack();
            } else {
                backpack2 = new Backpack(weight, volume);
                setUiOnAddsItem();
            }
        }
    }

    /**
     * Показывает пользователю первый созданный рюкзак.
     */
    private void showFirstBackpack() {
        firstBackpack = false;
        binding.textBackpack.setText(getString(R.string.helper_for_backpack_input_values, 2));
        binding.icBackpack1.setVisibility(View.VISIBLE);
        binding.currentBackpack1State.setText(getString(R.string.text_backpack_space_info,
                backpack1.capacityWeight,
                backpack1.capacityVolume));
        binding.currentBackpack1State.setVisibility(View.VISIBLE);
        binding.progressBackpack1.setVisibility(View.VISIBLE);
    }

    /**
     * Очистака полей ввода от введенного в них текста.
     */
    private void clearInput() {
        binding.inputVolume.setText("");
        binding.inputWeight.setText("");
        binding.inputName.setText("");
    }

    /**
     * Проверка пользовательского ввода в поля.
     * @return true - Если ввод корректный, false - иначе.
     */
    private boolean checkInput() {
        final Editable name;
        final Editable weight = binding.inputWeight.getText();
        final Editable volume = binding.inputVolume.getText();
        if (weight == null || volume == null) {
            return false;
        }
        if (TextUtils.isEmpty(weight) || TextUtils.equals(weight, ".")) {
            showSnackBar(getString(R.string.text_error_add_backpack_empty_weight));
            return false;
        } else if (TextUtils.isEmpty(volume) || TextUtils.equals(volume, ".")) {
            showSnackBar(getString(R.string.text_error_add_backpack_empty_volume));
            return false;
        }
        if (itemState) {
            name = binding.inputName.getText();
            if (name == null || TextUtils.isEmpty(name)) {
                showSnackBar(getString(R.string.text_error_add_item_empty_name));
                return false;
            }
        }
        return true;
    }

    /**
     * Скрывает открытую клавиатуру.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Показывает всплывающее сообщение пользователю.
     * @param text текст сообщения.
     */
    private void showSnackBar(String text) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Шаг 2 - Добавление предметов в рюкзаки.
     */
    private void setUiOnAddsItem() {
        itemState = true;
        binding.textBackpack.setText(getString(R.string.helper_for_item_input_values, numOfItems));
        binding.buttonTakeInput.setText(getString(R.string.text_button_add));
        binding.icBackpack2.setVisibility(View.VISIBLE);
        binding.currentBackpack2State.setText(getString(R.string.text_backpack_space_info,
                backpack2.capacityWeight,
                backpack2.capacityVolume));
        binding.currentBackpack2State.setVisibility(View.VISIBLE);
        binding.progressBackpack2.setVisibility(View.VISIBLE);
        binding.layoutInputName.setVisibility(View.VISIBLE);
        binding.listItemsBackpack1.setVisibility(View.VISIBLE);
        binding.listItemsBackpack2.setVisibility(View.VISIBLE);
        binding.buttonTakeInput.setOnClickListener(view -> addItems());
        weightCalculator = new WeightCalculator(backpack1, backpack2, getResources());
    }

    /**
     * Попытка добавления предмета в рюкзак
     */
    private void addItems() {
        if (checkInput()) {
            hideKeyboard();
            //noinspection ConstantConditions проверяется на null в checkInput()
            final String name = binding.inputName.getText().toString();
            //noinspection ConstantConditions проверяется на null в checkInput()
            final float weight = Float.parseFloat(binding.inputWeight.getText().toString());
            //noinspection ConstantConditions проверяется на null в checkInput()
            final float volume = Float.parseFloat(binding.inputVolume.getText().toString());
            final Item item = new Item(name, weight, volume);
            if (weightCalculator.addItemToBackpacks(item)) {
                numOfItems++;
                refreshBackpacksProgress();
                refreshBackpackItems();
                if (weightCalculator.checkOnWinCondition()) {
                    setCongratulationsUi();
                }
            }
            showSnackBar(weightCalculator.message);
            clearInput();
        }
    }

    /**
     * Шаг 3 - Рюкзаки полностью загружены.
     */
    private void setCongratulationsUi() {
        binding.textBackpack.setText(getString(R.string.congratulations));
        binding.buttonTakeInput.setVisibility(View.GONE);
        binding.layoutInputName.setVisibility(View.GONE);
        binding.layoutInputVolume.setVisibility(View.GONE);
        binding.layoutInputWeight.setVisibility(View.GONE);
        binding.currentBackpack1State.setVisibility(View.GONE);
        binding.currentBackpack2State.setVisibility(View.GONE);
        binding.progressBackpack1.setVisibility(View.GONE);
        binding.progressBackpack2.setVisibility(View.GONE);
    }

    /**
     *  Шаг 4 - Возвращение состояния UI к исходному
     */
    private void resetUi() {
        binding.layoutInputName.setVisibility(View.GONE);
        binding.buttonTakeInput.setOnClickListener(view -> setBackpackSize());
        binding.currentBackpack1State.setVisibility(View.INVISIBLE);
        binding.currentBackpack2State.setVisibility(View.INVISIBLE);
        binding.icBackpack1.setVisibility(View.INVISIBLE);
        binding.icBackpack2.setVisibility(View.INVISIBLE);
        binding.listItemsBackpack1.setVisibility(View.INVISIBLE);
        binding.listItemsBackpack2.setVisibility(View.INVISIBLE);
        binding.listItemsBackpack1.setText("");
        binding.listItemsBackpack2.setText("");
        binding.progressBackpack1.setVisibility(View.INVISIBLE);
        binding.progressBackpack1.setProgress(0);
        binding.progressBackpack2.setVisibility(View.INVISIBLE);
        binding.progressBackpack2.setProgress(0);
        binding.layoutInputWeight.setVisibility(View.VISIBLE);
        binding.layoutInputVolume.setVisibility(View.VISIBLE);
        binding.buttonTakeInput.setVisibility(View.VISIBLE);
        backpack1 = null;
        backpack2 = null;
        weightCalculator = null;
        firstBackpack = true;
        itemState = false;
        numOfItems = 1;
        binding.textBackpack.setText(getString(R.string.helper_for_backpack_input_values, 1  ));
        clearInput();
    }

    /**
     * Восстанавливает состояние с прошлой активности пользователя в приложении.
     */
    private void recoverState() {
        final String savedBackpack1 = preferences.getString(saveBackpack1, "");
        final String savedBackpack2 = preferences.getString(saveBackpack2, "");
        if (!savedBackpack1.equals("null")) {
            backpack1 = gson.fromJson(savedBackpack1, Backpack.class);
            showFirstBackpack();
            if (!savedBackpack2.equals("null")) {
                backpack2 = gson.fromJson(savedBackpack2, Backpack.class);
                numOfItems = preferences.getInt(saveNumOfItems, 1);
                setUiOnAddsItem();
                refreshBackpacksProgress();
                refreshBackpackItems();
                if (weightCalculator.checkOnWinCondition()) {
                    setCongratulationsUi();
                }
            }
        }
    }

    /**
     * Обновляет информацию о прогрессе заполнения рюкзаков в UI.
     */
    private void refreshBackpacksProgress() {
        binding.textBackpack.setText(getString(R.string.helper_for_item_input_values, numOfItems));
        binding.progressBackpack1.setProgress((int) weightCalculator.getLeftSpaceProgress(backpack1));
        binding.progressBackpack2.setProgress((int) weightCalculator.getLeftSpaceProgress(backpack2));
        binding.currentBackpack1State.setText(getString(R.string.text_backpack_space_info,
                weightCalculator.getLeftSpaceWeight(backpack1),
                weightCalculator.getLeftSpaceVolume(backpack1)));
        binding.currentBackpack2State.setText(getString(R.string.text_backpack_space_info,
                weightCalculator.getLeftSpaceWeight(backpack2),
                weightCalculator.getLeftSpaceVolume(backpack2)));
    }

    /**
     * Обновялет список предметов в рюкзаках в UI.
     */
    private void refreshBackpackItems() {
        binding.listItemsBackpack1.setText(backpack1.items);
        binding.listItemsBackpack2.setText(backpack2.items);
    }
}
