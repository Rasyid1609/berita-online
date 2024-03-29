package its.papsi.beritaonlinec.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import its.papsi.beritaonlinec.activity.NewsDetailActivity;
import its.papsi.beritaonlinec.R;
import its.papsi.beritaonlinec.helper.UtilMessage;
import its.papsi.beritaonlinec.adapter.NewsAdapter;
import its.papsi.beritaonlinec.model.News;

import static its.papsi.beritaonlinec.helper.GlobalVariable.BASE_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllNewsFragment extends Fragment {

    private RecyclerView rvList;
    private NewsAdapter newsAdapter;
    private UtilMessage utilMessage;

    public AllNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        rvList = view.findViewById(R.id.rv_list);

        utilMessage = new UtilMessage(getActivity());
        newsAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
        newsAdapter.setNewsAdapterListener(new NewsAdapter.NewsAdapterListener() {
            @Override
            public void onItemClickListener(News news) {
               // Toast.makeText(getContext(), "Clicked : " + news.getTitle(), Toast.LENGTH_LONG).show();
                Intent intentDetail = new Intent(getContext(), NewsDetailActivity.class);
                intentDetail.putExtra("data", news);
                startActivity(intentDetail);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(newsAdapter);

        getData();
    }

    private void getData() {
        utilMessage.showProgressBar("Getting news...");
        StringRequest request = new StringRequest(Request.Method.GET,
                BASE_URL + "ambil_berita.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utilMessage.dismissProgressBar();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonData = jsonResponse.getJSONArray("data");

                            ArrayList<News> data = new ArrayList<>();
                            for (int index = 0; index < jsonData.length(); index++) {
                                JSONObject item = jsonData.getJSONObject(index);

                                News news = new News();
                                news.setId(item.getString("id"));
                                news.setImageUrl(item.getString("gambar"));
                                news.setTitle(item.getString("judul"));
                                news.setContent(item.getString("isi"));
                                news.setAuthor(item.getString("nama_penulis"));
                                news.setCreatedAt(item.getString("created_at"));

                                data.add(news);
                            }

                            newsAdapter.setData(data);

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utilMessage.dismissProgressBar();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            getData();
        }

        return super.onOptionsItemSelected(item);
    }
}
