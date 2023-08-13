package tandoori.resturant.mobile.CountryPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import tandoori.resturant.mobile.R;

import java.util.List;

public class CountryListAdapter extends BaseAdapter {
    private Context mContext;
    List<Country> countries;
    LayoutInflater inflater;

    public CountryListAdapter(Context context, List<Country> countries) {
        super();
        this.mContext = context;
        this.countries = countries;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Country country = countries.get(position);

        if (view == null)
            view = inflater.inflate(R.layout.country_picker_row, null);

        Cell cell = Cell.from(view);
        cell.textView.setText(country.getName());
        cell.tvCountryCode.setText(country.getDialCode());
        country.loadFlagByCode(mContext);
        if (country.getFlag() != -1)
            cell.imageView.setImageResource(country.getFlag());
        return view;
    }

    static class Cell {
        public TextView tvCountryCode;

        public TextView textView;
        public ImageView imageView;

        static Cell from(View view) {
            if (view == null)
                return null;

            if (view.getTag() == null) {
                Cell cell = new Cell();
                cell.tvCountryCode = view.findViewById(R.id.tvCountryCode);
                cell.textView = view.findViewById(R.id.row_title);
                cell.imageView = view.findViewById(R.id.row_icon);
                view.setTag(cell);
                return cell;
            } else {
                return (Cell) view.getTag();
            }
        }
    }
}