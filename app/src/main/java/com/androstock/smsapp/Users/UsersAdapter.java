package com.androstock.smsapp.Users;




        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.RecyclerView.Adapter;
        import android.support.v7.widget.RecyclerView.ViewHolder;
        import android.view.LayoutInflater;

        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.view.animation.AlphaAnimation;
        import android.widget.Button;
        import android.widget.Filter;
        import android.widget.Filterable;
        import android.widget.ImageView;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.androstock.smsapp.R;

        import java.util.ArrayList;
        import java.util.List;

public class UsersAdapter extends Adapter<UsersAdapter.MyHolder> implements Filterable{
    private double amount = 0.0d;
    private Context context = null;
    private  List<UserModel> dataSet;
    private final ArrayList<String> data=new ArrayList<>();
    private List<UserModel> fragmentusermyordersGetterSetterList;
    private List<UserModel> originalList;
    private RecyclerView re;

    private final VenueAdapterClickCallbacks venueAdapterClickCallbacks;

   // String userid;



    public interface VenueAdapterClickCallbacks {
        void onCardClick(String userid);
    }




    public class MyHolder extends ViewHolder {
        final TextView name;
        final TextView phone;
        final TextView functions;

      //  final Button edit;
        final Button delete;

        public MyHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.phone = (TextView) itemView.findViewById(R.id.phone);
            this.functions = (TextView) itemView.findViewById(R.id.functions);
        //    this.edit=(Button)itemView.findViewById(R.id.edit);
            this.delete=(Button)itemView.findViewById(R.id.delete);


        //    SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
          //  userid = sharedPreferences.getString("id", "");

        }
    }

    public UsersAdapter(Context c, List<UserModel> data, VenueAdapterClickCallbacks venueAdapterClickCallback) {
        this.dataSet = data;
        this.venueAdapterClickCallbacks = venueAdapterClickCallback;
        this.context = c;
        this.fragmentusermyordersGetterSetterList = data;
        this.originalList=data;
    
        //this.pref = new PrefManager(this.context);
    }

    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myNewsHolder = new MyHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_list_items, parent, false));
        //this.re = (RecyclerView) parent.findViewById(R.id.cart_card_grid);
        return myNewsHolder;
    }

    public void onBindViewHolder(final MyHolder holder, final int position) {
        final TextView name = holder.name;
        final TextView functions = holder.functions;

        final TextView phone = holder.phone;
        name.setText("Name :"+((UserModel) this.dataSet.get(position)).getName());
        phone.setText("Phone :"+((UserModel) this.dataSet.get(position)).getUserphone());
        // contact.setText(""+((UserModel) this.dataSet.get(position)).getContact() +" PKR");
        functions.setText("Key :"+((UserModel) this.dataSet.get(position)).getUserkey());
       
        holder.delete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, dataSet.get(position).getUserphone(), Toast.LENGTH_SHORT).show();

                UsersAdapter.this.venueAdapterClickCallbacks.onCardClick(dataSet.get(position).getUserphone());
                dataSet.remove(position);


                notifyDataSetChanged();



            }
        });



    }
    public int getItemCount() {
        return this.dataSet.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataSet = (List<UserModel>) results.values;
                UsersAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<UserModel> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<UserModel> getFilteredResults(String constraint) {
        List<UserModel> results = new ArrayList<>();

        for (UserModel item : originalList) {
            if (item.getFunctionsassigned().toLowerCase().contains(constraint)
                    ||item.getUserphone().toLowerCase().contains(constraint)
                    ||item.getName().toLowerCase().contains(constraint)


                    ) {
                results.add(item);
            }
        }
        return results;
    }
    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 100);
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

}
