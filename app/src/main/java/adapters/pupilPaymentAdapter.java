package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.payment;
import pupilPackage.pupilPayment;
import teacherPackage.teacherPagesNavigator;

public class pupilPaymentAdapter extends RecyclerView.Adapter<adapters.pupilPaymentAdapter.AppViewHolder>{
    List<payment> payments;
    private Context context;
    private pupilPayment pupilPayment;
    public pupilPaymentAdapter(Context context, ArrayList<payment> payments,pupilPayment pupilPayment) {
        this.payments=payments;
        this.context=context;
        this.pupilPayment=pupilPayment;
    }

    public void setPayments(List<payment> payments) {
        this.payments = payments;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView paymentDate;
        TextView totalPayment;
        ImageView paymentAccepted;
        ImageView paymentNotAccepted;
        public AppViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.row);
            paymentDate = (TextView) itemView.findViewById(R.id.payDate);
            totalPayment = (TextView) itemView.findViewById(R.id.totalPay);
            paymentAccepted = (ImageView) itemView.findViewById(R.id.isAccepted);
            paymentNotAccepted = (ImageView) itemView.findViewById(R.id.notAccepted);
        }
    }
    @NonNull
    @Override
    public adapters.pupilPaymentAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_payment_row_adapter, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        adapters.pupilPaymentAdapter.AppViewHolder vh = new adapters.pupilPaymentAdapter.AppViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final adapters.pupilPaymentAdapter.AppViewHolder appViewHolder, int position1) {
        final int position=position1;
        final payment currentPayment=payments.get(position);
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(currentPayment.getPayDate());
        SimpleDateFormat simple=new SimpleDateFormat("dd-MM-yyyy");
        appViewHolder.paymentDate.setText(simple.format(currentPayment.getPayDate()));
        if(currentPayment.isAccepted()){
            appViewHolder.paymentAccepted.setVisibility(View.VISIBLE);
            appViewHolder.paymentNotAccepted.setVisibility(View.GONE);
        }else{
            appViewHolder.paymentAccepted.setVisibility(View.GONE);
            appViewHolder.paymentNotAccepted.setVisibility(View.VISIBLE);
            if(context instanceof teacherPagesNavigator)
            appViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal=Calendar.getInstance();
                    cal.setTime(currentPayment.getPayDate());
                    pupilPayment.showAddPaymentDialog(cal, currentPayment.getTotalPay());
                }
            });
        }
        appViewHolder.totalPayment.setText(currentPayment.getTotalPay().toString());
    }

    @Override
    public int getItemCount() {
        if(payments==null){
            return 0;
        }else
        {
            return payments.size();
        }
    }
}
