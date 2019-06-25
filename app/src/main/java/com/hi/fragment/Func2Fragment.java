package com.hi.fragment;

import android.view.View;
import android.widget.LinearLayout;

import com.hi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @描述 聊天界面功能页面2
 */
public class Func2Fragment extends BaseFragment {

    @Bind(R.id.llVoice)
    LinearLayout mLlVoice;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.frag_func_page2, null);
        ButterKnife.bind(this, view);
        return view;
    }
}
