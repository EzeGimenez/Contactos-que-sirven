package com.visoft.network.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.tab_search.ViewHolderRubro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.GlideApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RubroGeneral extends AbstractFlexibleItem<ViewHolderRubro> implements Serializable {

    private ArrayList<RubroEspecifico> subRubros;
    private String nombre;
    private int color;
    private String id;

    public RubroGeneral(Context context, String id) {
        this.id = id;

        int resId = context.getResources().getIdentifier(id, "string", context.getPackageName());
        this.nombre = context.getString(resId);

        resId = context.getResources().getIdentifier(
                id + "ID",
                "array",
                context.getPackageName()
        );

        subRubros = new ArrayList<>();
        String[] subRubrosID = context.getResources().getStringArray(resId);
        for (String a : subRubrosID) {
            subRubros.add(new RubroEspecifico(context, a));
        }
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rubro_button_layout;
    }

    @Override
    public ViewHolderRubro createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderRubro(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, final ViewHolderRubro holder, int position, List<Object> payloads) {
        holder.nombre.setText(nombre);

        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if (holder.img != null) {
            StorageReference userRef = storage.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME + "/" + id + ".png");
            GlideApp.with(holder.img.getContext())
                    .load(userRef)
                    .listener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Bitmap bitmap = null;
                            if (drawable instanceof BitmapDrawable) {
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                if (bitmapDrawable.getBitmap() != null) {
                                    bitmap = bitmapDrawable.getBitmap();
                                }
                            } else {

                                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                                    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
                                } else {
                                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                }

                                Canvas canvas = new Canvas(bitmap);
                                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                drawable.draw(canvas);
                            }

                            color = bitmap.getPixel(30, 30);

                            holder.c.setCardBackgroundColor(color);

                            return false;
                        }
                    })
                    .into(holder.img);
        }
    }

    public ArrayList<RubroEspecifico> getSubRubros() {
        return subRubros;
    }

    public String getId() {
        return id;
    }
}
