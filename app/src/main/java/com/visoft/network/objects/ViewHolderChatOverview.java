package com.visoft.network.objects;

import android.view.View;
import android.widget.TextView;

import com.visoft.network.R;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class ViewHolderChatOverview extends FlexibleViewHolder {

    public CircleImageView ivPic;
    public TextView tvMessage, tvName, tvTimeStamp;

    public ViewHolderChatOverview(View view, FlexibleAdapter adapter) {
        super(view, adapter);

        this.ivPic = view.findViewById(R.id.ivProfilePic);
        this.tvMessage = view.findViewById(R.id.tvLastMessage);
        this.tvName = view.findViewById(R.id.tvUsername);
        this.tvTimeStamp = view.findViewById(R.id.timeStamp);
    }
}