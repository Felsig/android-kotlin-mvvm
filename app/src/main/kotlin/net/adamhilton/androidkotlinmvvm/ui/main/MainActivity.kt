package net.adamhilton.androidkotlinmvvm.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.adamhilton.androidkotlinmvvm.App
import net.adamhilton.androidkotlinmvvm.R
import net.adamhilton.androidkotlinmvvm.injection.component.DaggerMainScreenComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @BindView(R.id.data)
    lateinit var tvData: TextView

    @Inject
    lateinit protected var viewModel: MainScreenViewModel

    lateinit private var subscriptions: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        initMainScreenComponent()

        subscriptions = CompositeDisposable()

        initBindings()
    }

    private fun initBindings() {
        subscriptions.addAll(
                viewModel.dataObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { it -> showData(it) }
        )
    }

    private fun initMainScreenComponent() {
        DaggerMainScreenComponent.builder()
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        subscriptions.clear()
    }

    @OnClick(R.id.get_data_button)
    fun getDataButtonClicked() {
        subscriptions.add(
            viewModel.loadData().subscribe()
        )
    }
    
    private fun showData(data: String) {
        tvData.text = data
    }
}
