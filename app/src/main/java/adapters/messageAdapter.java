package adapters;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import models.MessageModel;

public class messageAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> msgData;
    Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    final int SENDER_VIEWHOLDER = 0;
    final int RECEIVER_VIEWHOLDER = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();


    public messageAdapter(ArrayList<MessageModel> msgData, Context context) {

        this.msgData = msgData;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {

        if (msgData.get(position).getuId().equals(firebaseAuth.getUid()))
            return SENDER_VIEWHOLDER;
        else
            return RECEIVER_VIEWHOLDER;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEWHOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_listitem, parent, false);
            return new OutgoingViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_listitem, parent, false);
            return new IncomingViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getClass() == OutgoingViewholder.class) {
            ((OutgoingViewholder) holder).outgoingMsg.setText(msgData.get(position).getMsgText());

            if (msgData.get(position).getImageUri() != null) {
                ((OutgoingViewholder) holder).outgoing_image.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(msgData.get(position).getImageUri())
                        .into(((OutgoingViewholder) holder).outgoing_image);
            }

            long time = msgData.get(position).getMsgTime();
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            final String timeString =
                    new SimpleDateFormat("HH:mm").format(cal.getTime());

            ((OutgoingViewholder) holder).outgoingMsgTime.setText(timeString);
////////////////////////////////////////
            ((OutgoingViewholder) holder).outgoing_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(position);
                    ((OutgoingViewholder) holder).outgoing_pause.setVisibility(View.VISIBLE);
                    ((OutgoingViewholder) holder).outgoing_play.setVisibility(View.GONE);
                }
            });
            ((OutgoingViewholder) holder).outgoing_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pause(position);
                    ((OutgoingViewholder) holder).outgoing_pause.setVisibility(View.GONE);
                    ((OutgoingViewholder) holder).outgoing_play.setVisibility(View.VISIBLE);
                }
            });
            // if (!msgData.get(position).getImageUri().isEmpty()) {
            if (msgData.get(position).getImageUri().contains(".mp3")) {
                ((OutgoingViewholder) holder).outgoing_audiocontainer_nth.setVisibility(View.VISIBLE);
                ((OutgoingViewholder) holder).outgoing_pdf.setVisibility(View.GONE);
                ((OutgoingViewholder) holder).outgoing_image.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((OutgoingViewholder) holder).outgoing_audiocontainer_nth.setVisibility(View.GONE);
            }

            if (msgData.get(position).getImageUri().contains(".pdf")) {
                ((OutgoingViewholder) holder).outgoing_pdf.setVisibility(View.VISIBLE);
                ((OutgoingViewholder) holder).outgoing_audiocontainer_nth.setVisibility(View.GONE);
                ((OutgoingViewholder) holder).outgoing_image.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((OutgoingViewholder) holder).outgoing_pdf.setVisibility(View.GONE);
            }

            if (msgData.get(position).getImageUri().contains(".img")) {
                ((OutgoingViewholder) holder).outgoing_image.setVisibility(View.VISIBLE);
                ((OutgoingViewholder) holder).outgoing_audiocontainer_nth.setVisibility(View.GONE);
                ((OutgoingViewholder) holder).outgoing_pdf.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((OutgoingViewholder) holder).outgoing_image.setVisibility(View.GONE);
            }

            ((OutgoingViewholder) holder).outgoing_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (mediaPlayer != null) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
///////////////////////////////////////////////////
        } else {

            ((IncomingViewholder) holder).incomingMsg.setText(msgData.get(position).getMsgText());
            if (msgData.get(position).getImageUri() != null) {
                ((IncomingViewholder) holder).incomingImage.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(msgData.get(position).getImageUri())
                        .into(((IncomingViewholder) holder).incomingImage);
            }
            long time = msgData.get(position).getMsgTime();
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            final String timeString =
                    new SimpleDateFormat("HH:mm").format(cal.getTime());

            ((IncomingViewholder) holder).incomingMsgTime.setText(timeString);
            ((IncomingViewholder) holder).incoming_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(position);
                    ((IncomingViewholder) holder).incoming_pause.setVisibility(View.VISIBLE);
                    ((IncomingViewholder) holder).incoming_play.setVisibility(View.GONE);
                }
            });
            ((IncomingViewholder) holder).incoming_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pause(position);
                    ((IncomingViewholder) holder).incoming_pause.setVisibility(View.GONE);
                    ((IncomingViewholder) holder).incoming_play.setVisibility(View.VISIBLE);
                }
            });
            // if (!msgData.get(position).getImageUri().isEmpty()) {
            if (msgData.get(position).getImageUri().contains(".mp3")) {
                ((IncomingViewholder) holder).incoming_audiocontainer.setVisibility(View.VISIBLE);
                ((IncomingViewholder) holder).incoming_pdf.setVisibility(View.GONE);
                ((IncomingViewholder) holder).incomingImage.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((IncomingViewholder) holder).incoming_audiocontainer.setVisibility(View.GONE);
            }
            if (msgData.get(position).getImageUri().contains(".pdf")) {
                ((IncomingViewholder) holder).incoming_pdf.setVisibility(View.VISIBLE);
                ((IncomingViewholder) holder).incoming_audiocontainer.setVisibility(View.GONE);
                ((IncomingViewholder) holder).incomingImage.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((IncomingViewholder) holder).incoming_pdf.setVisibility(View.GONE);
            }
            if (msgData.get(position).getImageUri().contains(".img")) {
                ((IncomingViewholder) holder).incomingImage.setVisibility(View.VISIBLE);
                ((IncomingViewholder) holder).incoming_audiocontainer.setVisibility(View.GONE);
                ((IncomingViewholder) holder).incoming_pdf.setVisibility(View.GONE);

            } else if (msgData.get(position).getImageUri().isEmpty()) {
                ((IncomingViewholder) holder).incomingImage.setVisibility(View.GONE);
            }

            ((IncomingViewholder) holder).incoming_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (mediaPlayer != null) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }


    }

    private void pause(int position) {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void play(int position) {
        MessageModel audioUrl = msgData.get(position);
        try {
            mediaPlayer.setDataSource(audioUrl.getImageUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(false);
            Toast.makeText(context, "Audio Started Playing", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return msgData.size();
    }

    public class OutgoingViewholder extends RecyclerView.ViewHolder {

        TextView outgoingMsg, outgoingMsgTime;
        ImageView outgoing_image;
        FloatingActionButton outgoing_play, outgoing_pause;
        SeekBar outgoing_seekbar;
        ConstraintLayout outgoing_audiocontainer_nth;
        //PDFView outgoing_pdf;
        ImageView outgoing_pdf;


        public OutgoingViewholder(@NonNull View itemView) {
            super(itemView);

            outgoingMsg = itemView.findViewById(R.id.outgoing_msg);
            outgoingMsgTime = itemView.findViewById(R.id.outgoing_msg_time);
            outgoing_image = itemView.findViewById(R.id.outgoing_img);
            outgoing_play = itemView.findViewById(R.id.outgoing_play_nth);
            outgoing_pause = itemView.findViewById(R.id.outgoing_pause_nth);
            outgoing_seekbar = itemView.findViewById(R.id.outgoing_seekbar_nth);
            outgoing_pdf = itemView.findViewById(R.id.outgoing_pdf);
            outgoing_audiocontainer_nth = itemView.findViewById(R.id.outgoing_audiocontainer_nth);
        }
    }

    public class IncomingViewholder extends RecyclerView.ViewHolder {

        TextView incomingMsg, incomingMsgTime;
        ImageView incomingImage;
        //PDFView incoming_pdf;
        ImageView incoming_pdf;
        FloatingActionButton incoming_play, incoming_pause;
        SeekBar incoming_seekbar;
        ConstraintLayout incoming_audiocontainer;

        public IncomingViewholder(@NonNull View itemView) {
            super(itemView);

            incomingMsg = itemView.findViewById(R.id.incoming_msg);
            incomingMsgTime = itemView.findViewById(R.id.incoming_msg_time);
            incomingImage = itemView.findViewById(R.id.incoming_img);
            incoming_pdf = itemView.findViewById(R.id.incoming_pdf);
            incoming_play = itemView.findViewById(R.id.incoming_play_nth);
            incoming_pause = itemView.findViewById(R.id.incoming_pause_nth);
            incoming_seekbar = itemView.findViewById(R.id.incoming_seekbar_nth);
            incoming_audiocontainer = itemView.findViewById(R.id.incoming_audiocontainer_nth);
        }
    }
// audio recording

}
