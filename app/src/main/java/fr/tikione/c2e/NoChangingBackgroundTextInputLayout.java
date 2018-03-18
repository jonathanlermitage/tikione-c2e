package fr.tikione.c2e;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

/**
 * Created by tuxlu on 24/11/17.
 * https://stackoverflow.com/questions/40975232/do-not-change-textinputlayout-background-on-error
 */

public class NoChangingBackgroundTextInputLayout extends TextInputLayout {
    public NoChangingBackgroundTextInputLayout(Context context) {
        super(context);
    }

    public NoChangingBackgroundTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoChangingBackgroundTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setError(@Nullable CharSequence error) {
        //cette ligne est custom.
        ColorFilter defaultColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.CPCSecondColor),
                PorterDuff.Mode.MULTIPLY);
        super.setError(error);
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter);
    }

    @Override
    protected void drawableStateChanged() {
        ColorFilter defaultColorFilter = getBackgroundDefaultColorFilter();
        super.drawableStateChanged();
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter);
    }

    /**
     * If {@link #getEditText()} is not null & {@link #getEditText()#getBackground()} is not null,
     * update the {@link ColorFilter} of {@link #getEditText()#getBackground()}.
     *
     * @param colorFilter {@link ColorFilter}
     */
    private void updateBackgroundColorFilter(ColorFilter colorFilter) {
        if (getEditText() != null && getEditText().getBackground() != null)
            getEditText().getBackground().setColorFilter(colorFilter);
    }

    /**
     * Get the EditText's default background color.
     *
     * @return {@link ColorFilter}
     */
    @Nullable
    private ColorFilter getBackgroundDefaultColorFilter() {
        ColorFilter defaultColorFilter = null;
        if (getEditText() != null && getEditText().getBackground() != null)
            defaultColorFilter = DrawableCompat.getColorFilter(getEditText().getBackground());
        return defaultColorFilter;
    }
}