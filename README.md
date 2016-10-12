# ChatMessagesAdapter-android
QuickBlox ready-to-go chat messages adapter for Android chat applications that use Quickblox communication backend

# Screenshots

<img src="screenshots/screenshot1.png" border="5" alt="Chat Message Adapter" width="300">

# Dependencies
Just add to your build.gradle
```xml
dependencies {

compile 'com.quickblox:chat-message-adapter:1.0'

}
```
# Getting started
Example is included in repository. Try it out to see how chat message adapter works.

Steps to add QBMessagesAdapter to your Chat app:

1. Create a subclass of QBMessagesAdapter.
2. In your subclass define and override methods that you need (such as displayAvatarImage() and obtainAvatarUrl()).
3. Also you can adjust predefined styles for the next:
```xml
  * size of the avatar image view
  * message view container size
  * attachment view container size
  * bubble background for message
  * for all layouts and views set paddings, margins, etc.
```
## Style configuration
  
**ChatMessagesAdapter** has a flexible configuration system for displaying any view elements. For example:

To change bubble for left side opponent you can just create
```xml
    <style name="BubbleTextFrame.Left">
        <item name="android:background">@drawable/left_bubble</item>
    </style>
```
To change Avatar cell size
```xml
    <style name="AvatarImageViewStyle.Left">
        <item name="android:layout_width">@dimen/image_view_small_avatar_layout_width</item>
        <item name="android:layout_height">@dimen/image_view_small_avatar_layout_width</item>        
    </style>
```
To change some margin or padding
```xml
    <style name="ImageViewAttach.Left">
        <item name="android:layout_marginLeft">@dimen/padding_common</item>
    </style>
```

Some styles namespaces:
 * BubbleTextFrame (Right or Left) - for LinearLayout in widget text message with background bubble, that includes timetext, msgtext, widget views
 * BubbleAttachFrame (Right or Left) - for RelativeLayout in item attach with background bubble, that includes progressbar, image, timetext views
 * AvatarImageViewStyle (Right or Left) - for avatar CircleImageView
 * ListItemTextMessage (Right or Left) - for item text message MessageTextView
 * ListItemAttachMessage (Right or Left) - for item attach message
 * ProgressBarAttach (Right or Left) - for progressbar in attach
 * ImageViewAttach (Right or Left) - for image in attach
 * TextViewAttach (Right or Left) - for text in attach<br />
 <br />
 * WidgetTextMsgFrame - style for main FrameLayout text message
 * LinearTextMsgFrame - style for LinearLayout that includes all text message views
 
##Configure QBMessagesAdapter
You can modify layout resource files, that used in QBMessagesAdapter by creating them with the same namespaces.

In addition, you can add your own widget, just create the layout resource file.  
For example, create layout with namespace `list_item_text_right` with any layout, e.g. `text_widget_owner`:
```xml
<com.quickblox.ui.kit.chatmessage.adapter.widget.MessageTextViewRight
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:custom="http://schemas.android.com/apk/res-auto"

    android:id="@+id/msg_message_text_view_right"
    style="@style/ListItemTextMessage.Right"
    custom:widget_id="@layout/text_widget_owner">

</com.quickblox.ui.kit.chatmessage.adapter.widget.MessageTextViewRight>
```

Then you can extends QBMessagesAdapter and define your own logic for every item view - plain text message, message with image attachment or your custom item view:
```java
    @Override
    protected void onBindViewMsgRightHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        //update logic for showing your own plain text message
        TextView view = (TextView) holder.itemView.findViewById(R.id.custom_text_view);
        view.setText(currentUser.getFullName());
        super.onBindViewMsgRightHolder(holder, chatMessage, position);
    }
```
```java
    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
        //update logic for showing plain text message from opponent
    }
```
```java
    @Override
     protected void onBindViewAttachLeftHolder(ImageAttachHolder holder, QBChatMessage chatMessage, int position) {
        super.onBindViewAttachLeftHolder(holder, chatMessage, position);
        //update logic for showing message with image attachment from opponent
    }
```

Also you can override methods to display attach images
```xml
    @Override
    public void displayAttachment(QBMessageViewHolder holder, int position) {
        int preferredImageSizePreview = (int) (80 * Resources.getSystem().getDisplayMetrics().density);
        int valueType = getItemViewType(position);
        initGlideRequestListener((ImageAttachHolder) holder);

        QBChatMessage chatMessage = getItem(position);

        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        QBAttachment attachment = attachments.iterator().next();
        Glide.with(context)
                .load(attachment.getUrl())
                .listener(glideRequestListener)
                .override(preferredImageSizePreview, preferredImageSizePreview)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((ImageAttachHolder) holder).attachImageView);
    }
```
override methods to display avatars
```xml
    @Override
    public void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public String obtainAvatarUrl(int valueType, QBChatMessage chatMessage) {
        return currentUser.getId().equals(chatMessage.getSenderId()) ?
                currentUserData.getUserAvatar() : opponentUserData.getUserAvatar();
    }
```
and etc.
# License
See [LICENSE](LICENSE)
