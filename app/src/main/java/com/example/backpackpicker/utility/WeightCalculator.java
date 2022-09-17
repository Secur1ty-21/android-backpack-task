package com.example.backpackpicker.utility;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.example.backpackpicker.R;
import com.example.backpackpicker.model.Backpack;
import com.example.backpackpicker.model.Item;

public class WeightCalculator {
    private final Backpack backpack1;
    private final Backpack backpack2;
    public String message = "";
    private final Resources resources;

    public WeightCalculator(final Backpack backpack1, final Backpack backpack2, final Resources resources) {
        this.resources = resources;
        this.backpack1 = backpack1;
        this.backpack2 = backpack2;
    }

    /**
     * Добавляет предмет в рюкзак.
     *
     * @param item предмет для добавления в один из рюкзаков.
     * @return true - если предмет успешно добавлен, false - иначе.
     */
    public boolean addItemToBackpacks(@NonNull final Item item) {
        if (tryAddItemInBackpack(backpack1, item)) {
            message = resources.getString(R.string.text_successfully_add_item, item.name, 1);
            return true;
        }
        if (tryAddItemInBackpack(backpack2, item)) {
            message = resources.getString(R.string.text_successfully_add_item, item.name, 2);
            return true;
        }
        message = resources.getString(R.string.text_error_add_item);
        return false;
    }

    private boolean tryAddItemInBackpack(@NonNull final Backpack backpack, @NonNull final Item item) {
        if (backpack.capacityWeight - backpack.weightItems >= item.weight
                && backpack.capacityVolume - backpack.volumeItems >= item.volume) {
            backpack.items += resources.getString(R.string.text_item, item.name, item.weight, item.volume);
            backpack.weightItems += item.weight;
            backpack.volumeItems += item.volume;
            return true;
        }
        return false;
    }

    /**
     * @param backpack Рюкзак в котором необходимо узнать оставшееся место.
     * @return оставшееся место в рюкзаке(кг)
     */
    public float getLeftSpaceWeight(@NonNull final Backpack backpack) {
        return backpack.capacityWeight - backpack.weightItems;
    }

    /**
     * @param backpack Рюкзак в котором необходимо узнать оставшееся место.
     * @return оставшееся место в рюкзаке(л)
     */
    public float getLeftSpaceVolume(@NonNull final Backpack backpack) {
        return backpack.capacityVolume - backpack.volumeItems;
    }

    /**
     * @param backpack Рюкзак в котором необходимо узнать процент загруженности.
     * @return Загруженность рюкзака в процентах
     */
    public float getLeftSpaceProgress(@NonNull final Backpack backpack) {
        float onePercent = (backpack.capacityWeight + backpack.capacityVolume) / 100;
        return (backpack.weightItems + backpack.volumeItems) / onePercent;
    }

    /**
     * Проверка на полную загруженность рюкзаков
     *
     * @return true - если в рюкзаках не осталось свободного места, false - иначе.
     */
    public boolean checkOnWinCondition() {
        return getLeftSpaceWeight(backpack1) < 0.1f && getLeftSpaceVolume(backpack1) < 0.1f
                && getLeftSpaceWeight(backpack2) < 0.1f && getLeftSpaceVolume(backpack2) < 0.1f;
    }
}
