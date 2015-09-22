package org.phphub.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kennyc.view.MultiStateView;
import com.orhanobut.logger.Logger;

import org.phphub.app.R;
import org.phphub.app.api.entity.TopicEntity;
import org.phphub.app.api.entity.element.Topic;
import org.phphub.app.common.App;
import org.phphub.app.common.adapter.TopicItemView;
import org.phphub.app.common.base.BaseFragment;
import org.phphub.app.model.TopicModel;

import java.util.List;

import butterknife.Bind;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RecommendedFragment extends BaseFragment implements
        ViewEventListener<Topic>{
    TopicModel topicModel;

    RecyclerMultiAdapter adapter;

    @Bind(R.id.multiStateView)
    MultiStateView multiStateView;

    @Bind(R.id.recycler_view)
    RecyclerView topicListView;

    int pageIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicModel = new TopicModel(App.getInstance());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommended_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = SmartAdapter.empty()
                .map(Topic.class, TopicItemView.class)
                .listener(this)
                .into(topicListView);

        topicModel.getTopicsByExcellent(pageIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TopicEntity, List<Topic>>() {
                    @Override
                    public List<Topic> call(TopicEntity topicEntity) {
                        return topicEntity.getData();
                    }
                })
                .subscribe(new Action1<List<Topic>>() {
                    @Override
                    public void call(List<Topic> topics) {
                        adapter.setItems(topics);
                        multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                        multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                });
    }

    @Override
    protected String getTitle() {
        return getString(R.string.recommended);
    }

    @Override
    public void onViewEvent(int actionId, Topic topic, int position, View view) {

    }
}