package in.agilo.partner.runner.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.agilo.partner.runner.MainActivity;
import in.agilo.partner.runner.R;
import in.agilo.partner.runner.model.AppOrder;

/**
 * Created by Alessandro on 12/01/2016.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> implements View.OnClickListener{

    private int itemsCount = 0;
    private Context context;
    private Activity activity;


    private OnClickItemListener onClickItemListener;

    public ItemsAdapter(Context context, Activity currentActivity) {
        this.context = context;
        this.activity = currentActivity;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        itemViewHolder.llItem.setOnClickListener(this);
        itemViewHolder.btnCall.setOnClickListener(this);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        AppOrder order = ((MainActivity)activity).appOrders.get(position);
        holder.setName(order.getName()); // Name
        holder.setDescription(order.getTime()); // Description
        holder.tvAddress.setText(order.getAddress());
        holder.tvStatus.setText(order.getStatus());
        holder.llItem.setTag(position);
        holder.btnCall.setTag(position);
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        if (onClickItemListener != null) {
            switch (viewId) {
                case R.id.llItem:
                    onClickItemListener.onClickItem(v, (Integer) v.getTag());
                    break;
                case R.id.btnCall:
                    onClickItemListener.onClickCall(v, (Integer) v.getTag());
                    break;
            }
        }
    }

    public void updateItems(int itemsCount) {
        this.itemsCount = itemsCount;
        notifyDataSetChanged();
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.ivUser)
        ImageView ivUser;

        @InjectView(R.id.tvName)
        TextView tvName;

        @InjectView(R.id.tvDescription)
        TextView tvDescription;

        @InjectView(R.id.tvAddress)
        TextView tvAddress;

        @InjectView(R.id.tvStatus)
        TextView tvStatus;

        @InjectView(R.id.llItem)
        LinearLayout llItem;

        @InjectView(R.id.btnCall)
        ImageButton btnCall;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void setName(String name){
            tvName.setText(name);
        }

        public void setDescription(String description){
            tvDescription.setText(description);
        }

        public void setImage(int idImage){
            Picasso.with(ivUser.getContext()).
                    load(idImage).
                    centerCrop().
                    resize(60,60).
                    transform(new CircleTransform()).
                    into(ivUser);
        }

    }

    public interface OnClickItemListener {

        public void onClickItem(View v, int position);

        public void onClickCall(View v, int position);

    }


}
