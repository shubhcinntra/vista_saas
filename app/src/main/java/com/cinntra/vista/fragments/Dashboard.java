package com.cinntra.vista.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vista.EasyPrefs.Prefs;
import com.cinntra.vista.R;
import com.cinntra.vista.activities.BussinessPartners;
import com.cinntra.vista.activities.CampaignActivity;
import com.cinntra.vista.activities.DeliveryActivity;
import com.cinntra.vista.activities.ExpenseActivity;
import com.cinntra.vista.activities.InventoryActivity;
import com.cinntra.vista.activities.InvoiceActivity;
import com.cinntra.vista.activities.LeadsActivity;
import com.cinntra.vista.activities.Notifications;
import com.cinntra.vista.activities.Opportunities_Pipeline_Activity;
import com.cinntra.vista.activities.OrderActivity;
import com.cinntra.vista.activities.PaymentDetails;
import com.cinntra.vista.activities.QuotationActivity;
import com.cinntra.vista.adapters.DeliveryItemAdapter;
import com.cinntra.vista.adapters.GridViewAdapter;
import com.cinntra.vista.adapters.InventoryAdapter;
import com.cinntra.vista.databinding.FragmentDashboard2Binding;
import com.cinntra.vista.globals.Globals;
import com.cinntra.vista.interfaces.ChangeTeam;
import com.cinntra.vista.model.CounterResponse;
import com.cinntra.vista.model.CountryData;
import com.cinntra.vista.model.CountryResponse;
import com.cinntra.vista.model.EmployeeValue;
import com.cinntra.vista.model.FastMovingItems;
import com.cinntra.vista.model.HeirarchiResponse;
import com.cinntra.vista.model.IndustryItem;
import com.cinntra.vista.model.InventoryResponse;
import com.cinntra.vista.model.SaleEmployeeResponse;
import com.cinntra.vista.model.SalesEmployeeItem;
import com.cinntra.vista.room.CountriesDatabase;
import com.cinntra.vista.room.IndustriesDatabase;
import com.cinntra.vista.viewModel.ItemViewModel;
import com.cinntra.vista.webservices.NewApiClient;
import com.cinntra.vista.workManager.ApiSchedular;
import com.cinntra.vista.workManager.BackgroundLocationService;
import com.cinntra.vista.workManager.LocationServiceBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends Fragment implements View.OnClickListener, ChangeTeam {

    DeliveryItemAdapter adpt;
    InventoryAdapter intAdpt;


    String userTyep;
    /*String []mainItems = {"Lead Generation/Opportunities","Inventory","Sales Quotation",
    "Sales Order","Delivery","Invoice "};*/
    String[] mainItems = {"Opportunities", "Quotations", "Orders", "Invoice "};
    String[] inventoryItem = {"Fast Moving", "Slow Moving", "Non Moving"};
    int[] inventoryIcons = {R.drawable.ic_inventory, R.drawable.ic_inventory, R.drawable.ic_inventory};

    String[] deliveryItem = {"Overdue", "Open", "Closed"};
    String[] colorarray = {"#F3425F", "#763EE7", "#1878F3"};
    int[] deliveryIcons = {R.drawable.ic_delivery_overdue, R.drawable.ic_delivery_open, R.drawable.ic_delivery_closed};

    int[] images = {R.drawable.ic_opportunity, R.drawable.ic_quotation, R.drawable.ic_order, R.drawable.ic_invoice};

    FragmentDashboard2Binding binding;

    public Dashboard() {
        //Required empty public constructor

    }


    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    private void doSomethingWithGithubShubham() {
        Toast.makeText(requireActivity(), "Do Something", Toast.LENGTH_SHORT).show();
    }

    private void doInShubhNewBranch() {
        Toast.makeText(requireActivity(), "Do Nothing", Toast.LENGTH_SHORT).show();
    }


    private void doSomethingWithNewBranchGithubShubham() {
        Toast.makeText(requireActivity(), "Do Something New Branch", Toast.LENGTH_SHORT).show();
    }



    Handler handler = new Handler();
    Runnable runnable;

    @Override
    public void onResume() {
        super.onResume();
        if (Prefs.getBoolean(Globals.Location_Boolean_MInutes, false)) {
            // handler.removeCallbacks(runnable);
            Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show();
        } else {
            //  handler.postDelayed(runnable,1000);
        }
        handler.post(runnable);
//handler.postDelayed()

        setDashboardCounters();
        binding.userRoleTxt.setText(Prefs.getString(Globals.SalesEmployeeName, ""));
        Globals.inventory_item_close = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = FragmentDashboard2Binding.inflate(inflater, container, false);
        View v = inflater.inflate(R.layout.fragment_dashboard2, container, false);
        // ButterKnife.bind(this, v);
        Globals.CURRENT_CLASS = getClass().getName();

        Prefs.putBoolean(Globals.Location_Boolean_MInutes, false);

        //todo calling job schedular for current location in every 15 minutes.
        if (Prefs.getBoolean(Globals.Location_FirstTime, false)) {
            ApiSchedular.schedularCall(getActivity());
        }

        //todo
        Intent intent = new Intent(getActivity(), BackgroundLocationService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        startStopService();
        Globals.LocationShared = true;

        enableMyLocation();

        userTyep = Prefs.getString(Globals.USER_TYPE, "manager");

        if (Globals.checkInternet(getContext())) {
            callinventorylistapi();
        }
        setDefaults(userTyep);
       // binding.userName.setText(Prefs.getString(Globals.Employee_Name, ""));
        binding.proPic.setText(String.valueOf(Prefs.getString(Globals.Employee_Name, "").charAt(0)).toUpperCase());

        return binding.getRoot();
    }


    private void startStopService() {
        getActivity().startService(new Intent(getActivity(), BackgroundLocationService.class));
        // Uncomment the following code block if you want to stop the service
    /*
    if (isMyServiceRunning(BackgroundLocationService.class)) {
        Toasty.success(requireContext(), "stop service").show();
        getActivity().stopService(new Intent(getActivity(), BackgroundLocationService.class));
    } else {
        Toasty.success(requireContext(), "start service").show();
        getActivity().startService(new Intent(getActivity(), BackgroundLocationService.class));
    }
    */
    }

    private Boolean isBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationServiceBinder binder = (LocationServiceBinder) service;
            isBound = true;

            // Now you can access totalDistanceInMeters from the service

            // Toast.makeText(requireContext(), String.valueOf(totalDistance), Toast.LENGTH_SHORT).show();
            // Use the totalDistance as needed in your fragment
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private void callinventorylistapi() {

        HashMap<String, String> st = new HashMap<>();
        st.put("SalesEmployeeCode", Globals.TeamEmployeeID);


        Call<InventoryResponse> call = NewApiClient.getInstance().getApiService(requireActivity()).getInventorylist(st);
        call.enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                        Dashboard.fastInventoryList.clear();
                        Dashboard.mediumInventoryList.clear();
                        Dashboard.nonInventoryList.clear();
                        Dashboard.allInventoryList.clear();

                        // Check for null before adding to avoid NullPointerException
                        List<FastMovingItems> fastMovingItems = response.body().getData().get(0).getFastMovingItemsList();
                        if (fastMovingItems != null) {
                            Dashboard.fastInventoryList.addAll(fastMovingItems);
                        }

                        List<FastMovingItems> slowMovingItems = response.body().getData().get(0).getSlowMovingItemsList();
                        if (slowMovingItems != null) {
                            Dashboard.mediumInventoryList.addAll(slowMovingItems);
                        }

                        List<FastMovingItems> notMovingItems = response.body().getData().get(0).getNotMovingItemsList();
                        if (notMovingItems != null) {
                            Dashboard.nonInventoryList.addAll(notMovingItems);
                        }

                        // Combine all lists into allInventoryList
                        Dashboard.allInventoryList.addAll(Dashboard.fastInventoryList);
                        Dashboard.allInventoryList.addAll(Dashboard.mediumInventoryList);
                        Dashboard.allInventoryList.addAll(Dashboard.nonInventoryList);
                    } else {
                        // Handle case where response body or data list is null
                        Toast.makeText(requireContext(), "No inventory data available.", Toast.LENGTH_SHORT).show();
                    }

                    // Notify the adapter about data changes
                    intAdpt.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }






            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                //Toast.makeText(requireContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static ArrayList<CountryData> countrylist = new ArrayList<>();

    private void setDefaults(String type) {

        // callCountryApi();
       /* if(!Globals.TeamRole.equalsIgnoreCase("salesman")) {

        }else{
            userRole.setVisibility(View.GONE);
        }*/
        loadHeirarchi();

        loadAllSalesEmployee();


        GridViewAdapter adapter = new GridViewAdapter(getActivity(), mainItems, images);
        binding.grid.setAdapter(adapter);

        binding.viewAllDelivery.setOnClickListener(this);
        binding.seeAll.setOnClickListener(this);
        binding.opportunityCard.setOnClickListener(this);
        binding.quotationCard.setOnClickListener(this);
        binding.orderCard.setOnClickListener(this);
        binding.invoicesCard.setOnClickListener(this);
        binding.userRole.setOnClickListener(this);
        binding.notification.setOnClickListener(this);
        binding.customerCard.setOnClickListener(this);
        binding.proPic.setOnClickListener(this);
        binding.notificationView.setOnClickListener(this);
        binding.leadCard.setOnClickListener(this);
        binding.campaignCard.setOnClickListener(this);
        binding.expenseCard.setOnClickListener(this);
        binding.paymentdetailsCard.setOnClickListener(this);


        intAdpt = new InventoryAdapter(getActivity(), inventoryItem, inventoryIcons);
        binding.invetoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.invetoryRecycler.setAdapter(intAdpt);
        binding.invetoryRecycler.smoothScrollToPosition(0);
        intAdpt.notifyDataSetChanged();


    }


    private void callCountryApi() {

        Call<CountryResponse> call = NewApiClient.getInstance().getApiService(requireActivity()).getCountryList();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        countrylist.clear();
                        countrylist.addAll(response.body().getData());
                    }


                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_all_delivery:
                startActivity(new Intent(getActivity(), DeliveryActivity.class));
                break;
            case R.id.see_all:
                Intent i = new Intent(getActivity(), InventoryActivity.class);
                i.putExtra("IN_Type", "All");
                startActivity(i);
                break;

            case R.id.cardProjectSales:
                CountriesDatabase db = CountriesDatabase.getDatabase(getActivity());
                IndustriesDatabase dbIndustry = IndustriesDatabase.getDatabase(getActivity());

                List<CountryData> localData = db.myDataDao().getAll();
                List<IndustryItem> industryData = dbIndustry.industriesDao().getAll();
                Log.d("TAG", "firstValue: " + localData.get(0).getName());
                Log.d("TAG", "firstIndustryValue: " + industryData.get(3).getIndustryName());
                break;

            case R.id.lead_card:
                Prefs.putString(Globals.BussinessPageType, "DashBoard");
                startActivity(new Intent(getActivity(), LeadsActivity.class));
                break;

            case R.id.opportunity_card:
                // startActivity(new Intent(getActivity(), OpportunitiesActivity.class));
                Prefs.putString(Globals.SelectOpportnity, "Dashboard");
                startActivity(new Intent(getActivity(), Opportunities_Pipeline_Activity.class));
                break;

            case R.id.quotation_card:
                Prefs.putString(Globals.QuotationListing, "null");
                Prefs.putString(Globals.SelectQuotation, "Dashboard");
                startActivity(new Intent(getActivity(), QuotationActivity.class));
                break;
            case R.id.order_card:
                startActivity(new Intent(getActivity(), OrderActivity.class));
                break;
            case R.id.invoices_card:
                startActivity(new Intent(getActivity(), InvoiceActivity.class));
                break;
            case R.id.campaign_card:
                startActivity(new Intent(getActivity(), CampaignActivity.class));
                break;
            case R.id.expense_card:
                startActivity(new Intent(getActivity(), ExpenseActivity.class));
                break;

            case R.id.paymentdetails_card:
                startActivity(new Intent(getActivity(), PaymentDetails.class));
                break;
            case R.id.userRole:
                //createPdf();
                showBottomSheet();
                break;
            case R.id.notification:
            case R.id.notification_view:
                // openNotification();
                //startActivity(new Intent(getActivity(), Navigation_drawer.class));
                // startActivity(new Intent(getActivity(), AdminMainActivity.class));
                // startActivity(new Intent(getActivity(), MapsActivity.class));
                startActivity(new Intent(getActivity(), Notifications.class));
                break;
            case R.id.customer_card:
                Prefs.putString(Globals.BussinessPageType, "DashBoard");
                startActivity(new Intent(getActivity(), BussinessPartners.class));
                break;
            case R.id.pro_pic:
                // startActivity(new Intent(getActivity(), AdminMainActivity.class));
                break;
        }
    }


    private void showBottomSheet() {

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(Dashboard.this);
        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());

    }

    private void setDashboardCounters() {
        dashboardcounter();
    }


    public static ArrayList<EmployeeValue> teamList_Hearchi = new ArrayList<>();

    private void loadHeirarchi() {
        EmployeeValue employeeValue = new EmployeeValue();
        employeeValue.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<HeirarchiResponse> call = NewApiClient.getInstance().getApiService(requireActivity()).getAllEmployeelist(employeeValue);
        call.enqueue(new Callback<HeirarchiResponse>() {
            @Override
            public void onResponse(Call<HeirarchiResponse> call, Response<HeirarchiResponse> response) {
                if (response != null) {
                    teamList_Hearchi.clear();
                    if (response.body().getData().size() > 0) {
                        teamList_Hearchi.addAll(response.body().getData());
                    }

                }
            }

            @Override
            public void onFailure(Call<HeirarchiResponse> call, Throwable t) {

            }
        });
    }


    public static ArrayList<SalesEmployeeItem> salesAllList_Hearchi = new ArrayList<>();

    private void loadAllSalesEmployee() {

        Call<SaleEmployeeResponse> call = NewApiClient.getInstance().getApiService(requireActivity()).getSalesEmplyeeList();
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {
                if (response != null) {
                    salesAllList_Hearchi.clear();
                    if (response.body().getValue().size() > 0) {
                        salesAllList_Hearchi.addAll(response.body().getValue());
                    }

                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {
                Log.e("Failure_sales==>", "onFailure: " + t.getMessage() );
            }
        });
    }


    private void dashboardcounter() {
        SalesEmployeeItem salesEmployeeItem = new SalesEmployeeItem();
        salesEmployeeItem.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<CounterResponse> call = NewApiClient.getInstance().getApiService(requireActivity()).dashboardcounter(salesEmployeeItem);
        call.enqueue(new Callback<CounterResponse>() {
            @Override
            public void onResponse(Call<CounterResponse> call, Response<CounterResponse> response) {
                if (response != null) {
                    if (response.body().getValue().size()>0){
                        String[] deliverycounter = new String[3];
                        deliverycounter[0] = response.body().getValue().get(0).getOver();
                        deliverycounter[1] = response.body().getValue().get(0).getOpen();
                        deliverycounter[2] = response.body().getValue().get(0).getClose();
                        binding.deliveryRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                        adpt = new DeliveryItemAdapter(getActivity(), deliveryItem, deliveryIcons, deliverycounter);
                        binding.deliveryRecycler.setAdapter(adpt);
                        adpt.notifyDataSetChanged();
                        binding.leadCounter.setText(response.body().getValue().get(0).getLeads());
                        binding.balnce.setText("₹ " + response.body().getValue().get(0).getSale());
                        binding.differentSale.setText("₹ " + response.body().getValue().get(0).getSale_diff());
                        binding.projectSale.setText("₹ " + response.body().getValue().get(0).getAmount());
                        binding.opportunityCounter.setText(response.body().getValue().get(0).getOpportunity());
                        binding.quotationCounter.setText(response.body().getValue().get(0).getQuotation());
                        binding.orderCounter.setText(response.body().getValue().get(0).getOrder());
                        binding.customerCounter.setText(response.body().getValue().get(0).getCustomer());
                        binding.campaignCounter.setText(response.body().getValue().get(0).getCampaign_count());
                        binding.expenseCounter.setText(response.body().getValue().get(0).getExpense_all());
                        binding.paymentdetailsCounter.setText(response.body().getValue().get(0).getPayment_all());
                        if (response.body().getValue().get(0).getNotification().equalsIgnoreCase("0")) {
                            binding.countLayout.setVisibility(View.GONE);
                        } else {
                            binding.countLayout.setVisibility(View.VISIBLE);
                            binding.count.setText(response.body().getValue().get(0).getNotification());
                        }
                        adpt.notifyDataSetChanged();

                        Invoicescounter();
                    }


                }
            }

            @Override
            public void onFailure(Call<CounterResponse> call, Throwable t) {

            }
        });
    }


    private void Invoicescounter() {
        SalesEmployeeItem salesEmployeeItem = new SalesEmployeeItem();
        salesEmployeeItem.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<CounterResponse> call = NewApiClient.getInstance().getApiService(getActivity()).InvoicesCount(salesEmployeeItem);
        call.enqueue(new Callback<CounterResponse>() {
            @Override
            public void onResponse(Call<CounterResponse> call, Response<CounterResponse> response) {
                if (response != null && response.code() == 200) {
                    if (response.body().getValue().size() > 0)
                        binding.invoicesCounter.setText(response.body().getValue().get(0).getInvoice());
                    else
                        binding.invoicesCounter.setText("0");


                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CounterResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }


    /******************** Save User ID**************************/


    /************************ Delievry Counter ************************/


    public static ArrayList<FastMovingItems> fastInventoryList = new ArrayList<>();
    public static ArrayList<FastMovingItems> mediumInventoryList = new ArrayList<>();
    public static ArrayList<FastMovingItems> nonInventoryList = new ArrayList<>();
    public static ArrayList<FastMovingItems> allInventoryList = new ArrayList<>();


    private void callSalesApi() {
        ItemViewModel model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getSalesEmployeeList().observe(getActivity(), new Observer<List<SalesEmployeeItem>>() {
            @Override
            public void onChanged(@Nullable List<SalesEmployeeItem> itemsList) {
                if (itemsList != null) {
                    Globals.saveSaleEmployeeArrayList(itemsList, Globals.SalesEmployeeList);
                }

            }
        });
    }


    private void openNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "NewNotification")
                .setContentTitle("Hey,")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("You are meeting with Cinntra! ");
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
        managerCompat.notify(999, builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("NewNotification", "NewNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void changeTeam(String name) {
        binding.userRoleTxt.setText(name);
        setDashboardCounters();
    }


    int REQUEST_LOCATION_PERMISSION = 1;

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("access===>", "access");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
//            mMap.setMyLocationEnabled(true);
        } else {
            Log.e("denied===>", "denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation();
            }
        }
    }


}