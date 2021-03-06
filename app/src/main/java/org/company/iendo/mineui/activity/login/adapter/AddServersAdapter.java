package org.company.iendo.mineui.activity.login.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.company.iendo.R;
import org.company.iendo.common.MyAdapter;
import org.company.iendo.bean.beandb.ServersDBBean;

/**
 * LoveLin
 * <p>
 * Describe 添加服务器的adapter
 */
public class AddServersAdapter extends MyAdapter<ServersDBBean> {

    public AddServersAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends MyAdapter.ViewHolder {

        private TextView mID;
        private TextView mName;
        private TextView mIP;
        private TextView mPort;

        //获取item布局,查找布局,进行业务处理
        public ViewHolder() {
            super(R.layout.item_add_servers_content);
            mID = (TextView) findViewById(R.id.add_id);
            mName = (TextView) findViewById(R.id.add_name);
            mIP = (TextView) findViewById(R.id.add_ip);
            mPort = (TextView) findViewById(R.id.add_port);
        }

        //数据回写
        @Override
        public void onBindView(int position) {
            mID.setText(position + "");
            mName.setText(getItem(position).getName());
            mIP.setText(getItem(position).getIp());
            mPort.setText(getItem(position).getPort());


        }
    }


}
