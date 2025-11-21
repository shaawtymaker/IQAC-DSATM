package info.androidhive.navigationdrawer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// ✅ ADDED: This fixes "Cannot find symbol AppCompatActivity"
import androidx.appcompat.app.AppCompatActivity;

// ✅ FIXED: Removed the extra .LinearLayoutManager at the end
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.other.Utils;

public class ChatActivity extends AppCompatActivity {

    private final String mChannelUrl = "sendbird_open_channel_52457_b13e7d6ce491bc9723a1f34eb086356992c7b5e4";
    private final static String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER";

    private ChatAdapter mChatAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Button mSendButton;
    private EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // This line works now because we imported AppCompatActivity correctly
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSendButton = (Button) findViewById(R.id.button_chat_send);
        mMessageEditText = (EditText) findViewById(R.id.edittext_chat);

        mRecyclerView = (RecyclerView) findViewById(R.id.reycler_chat);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        OpenChannel.getChannel(mChannelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(final OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        };

                        mChatAdapter = new ChatAdapter(openChannel);
                        mRecyclerView.setAdapter(mChatAdapter);
                    }
                });
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatAdapter.sendMessage(mMessageEditText.getText().toString());
                mMessageEditText.setText("");
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    mChatAdapter.loadPreviousMessages();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Receives messages from SendBird servers
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseChannel.getUrl().equals(mChannelUrl) && baseMessage instanceof UserMessage) {
                    mChatAdapter.appendMessage((UserMessage) baseMessage);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    private class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

        private ArrayList<BaseMessage> mMessageList;
        private OpenChannel mChannel;

        ChatAdapter(OpenChannel channel) {
            mMessageList = new ArrayList<>();
            mChannel = channel;

            refresh();
        }

        // Retrieves 30 most recent messages.
        void refresh() {
            mChannel.getPreviousMessagesByTimestamp(Long.MAX_VALUE, true, 30, true,
                    BaseChannel.MessageTypeFilter.USER, null, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                            mMessageList = (ArrayList<BaseMessage>) list;

                            notifyDataSetChanged();
                        }
                    });
        }

        void loadPreviousMessages() {
            final long lastTimestamp = mMessageList.get(mMessageList.size() - 1).getCreatedAt();
            mChannel.getPreviousMessagesByTimestamp(lastTimestamp, false, 30, true,
                    BaseChannel.MessageTypeFilter.USER, null, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                            mMessageList.addAll(list);

                            notifyDataSetChanged();
                        }
                    });
        }

        // Appends a new message to the beginning of the message list.
        void appendMessage(UserMessage message) {
            mMessageList.add(0, message);
            notifyDataSetChanged();
        }

        // Sends a new message, and appends the sent message to the beginning of the message list.
        void sendMessage(final String message) {
            mChannel.sendUserMessage(message, new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }

                    mMessageList.add(0, userMessage);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            UserMessage message = (UserMessage) mMessageList.get(position);

            if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            UserMessage message = (UserMessage) mMessageList.get(position);

            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(message);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(message);
            }
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }

        private class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            }

            void bind(UserMessage message) {
                messageText.setText(message.getMessage());
                timeText.setText(Utils.formatTime(message.getCreatedAt()));
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText, nameText;
            ImageView profileImage;

            ReceivedMessageHolder(View itemView) {
                super(itemView);
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
                nameText = (TextView) itemView.findViewById(R.id.text_message_name);
                profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            }

            void bind(UserMessage message) {
                messageText.setText(message.getMessage());
                nameText.setText(message.getSender().getNickname());
                Utils.displayRoundImageFromUrl(ChatActivity.this,
                        message.getSender().getProfileUrl(), profileImage);
                timeText.setText(Utils.formatTime(message.getCreatedAt()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}