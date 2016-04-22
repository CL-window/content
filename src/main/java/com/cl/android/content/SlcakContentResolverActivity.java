package com.cl.android.content;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by chenling on 2016/3/20.
 * ContentResolver 操作自定义的数据
 * */
public class SlcakContentResolverActivity extends AppCompatActivity {

    private ListView listView;
    private Uri uri;
    private EditText username;
    private ContentValues values;
    private static final int EDIT_ID = 1;//编辑
    private static final int DELETE_ID = 2;//删除

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.dataListView);
        username = (EditText)findViewById(R.id.username);

        uri = Uri.parse("content://com.slack.cl.User_Info_Provider"); //自定义的URi ,这个是 Restful 风格的
        values = new ContentValues();

        /**
         * ContextMenu用户手指长按某个View触发的菜单
         * 实现场景：用户长按某个List元素，则弹出ContextMenu，选择菜单“Delete”，按下后，弹出AlertDialog，
         * 请用户再去确定是否删除，确定后将数据从SQLite中删除，并更新ListView的显示。
         * */
        //向ListView注册Context Menu，当系统检测到用户长按某单元是，触发Context Menu弹出
        registerForContextMenu(listView);
    }

    // 步骤2：创建ContextMenu同OptionMenu，用户长按元素后，会弹出菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, EDIT_ID , Menu.NONE, "Edit");
        menu.add(Menu.NONE, DELETE_ID , Menu.NONE, "Delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    //步骤 3: ContextMenu的触发操作，例子将触发delete() 还可以做编辑等待
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        /* 在此处，我们关键引入 AdapterView.AdapterContextMenuInfo来获取单元的信息。
             在有三个重要的信息。
              1、id：The row id of the item for which the context menu is being displayed ，
                 在cursorAdaptor中，实际就是表格的_id序号；
              2、position 是list的元素的顺序；
              3、view就可以获得list中点击元素的View， 通过view可以获取里面的显示的信息
           */
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            case DELETE_ID:
                delete(info.id);
                return true;
            case EDIT_ID:

//                edit(info.id);
                return true;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    //步骤4: 对触发弹框，和Add的相似，确定后，更新数据库和更新ListView的显示，上次学习已有相类的例子，不再重复。其中getNameById是通过id查名字的方法。值得注意的是，为了内部类中使用，delete的参数采用来final的形式。
    private void delete(final long  rowId){
        if(rowId>0){
            new AlertDialog.Builder(this)
                    .setTitle("确定要删除?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String [] selectionArgs = new String[1];
                            selectionArgs[0] = String.valueOf(rowId);
                            String where = "_id = ?";
                            //public final int delete (Uri url, String where, String[] selectionArgs)
                            getContentResolver().delete(uri,where,selectionArgs);
                            selectAllInfo();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }
    //查询所有数据的按钮
    public void selectAllData(View view) {
        Log.i("slack", "selectAllData SlcakContentResolverActivity..........");
        selectAllInfo();
    }
    //查询所有的数据，在listView里显示
    private void selectAllInfo() {
//        Log.i("slack", "selectAllInfo SlcakContentResolverActivity..........");
        //public final Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
//        Log.i("slack", "query done SlcakContentResolverActivity..........");
        //这样是可以的，但是个人感觉好麻烦呀,而且删除编辑时id不好处理，除非我弄一个对象处理
      /*
        ArrayList<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

         while (cursor.moveToNext()){
           Map<String, Object> map = new HashMap<String, Object>();
           map.put("username", cursor.getString(cursor.getColumnIndex("username")));

           listems.add(map);
//           Log.i("slack",cursor.getString(cursor.getColumnIndex("id"))+"   :"+cursor.getString(cursor.getColumnIndex("username")));
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                this,
                listems,
                R.layout.item,
                new String[] {"username"},
                new int[] {R.id.textView}
        );*/
        // 用 CursorAdapter 必须是 _id 做为id的列名
        CursorAdapter cursorAdapter= new CursorAdapter(getApplicationContext(),cursor,true) {
            //新建一个视图来保存cursor指向的数据
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                //找到布局和控件
//                LayoutInflater inflater =  LayoutInflater.from(context);
                //一般都这样写，返回列表行元素，注意这里返回的就是bindView中的view
                return LayoutInflater.from(context).inflate(R.layout.item,parent,false);
            }
            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                String username = cursor.getString(cursor.getColumnIndex("username"));
                TextView textView = (TextView)view.findViewById(R.id.textView);
                textView.setText(username);

            }
        };
        Log.i("slack", "selectAllInfo done SlcakContentResolverActivity..........");
//        listView.setAdapter(mSimpleAdapter);
        listView.setAdapter(cursorAdapter);

    }
    public void addAData(View view) {
        Log.i("slack", "addAData SlcakContentResolverActivity..........");
        if(!TextUtils.isEmpty(username.getText().toString())){
            Log.i("slack", "isEmpty username SlcakContentResolverActivity..........");
            values.clear();
            values.put("username", username.getText().toString());
            getContentResolver().insert(uri, values);
            Log.i("slack", "getContentResolver().insert SlcakContentResolverActivity..........");
            //添加完查询一次，数据量要是大，不可以这么查，上滑下拉刷新
            selectAllInfo();
        }else{
            Toast.makeText(this, "username null", Toast.LENGTH_SHORT).show();
        }

    }
}
