package net.lim.view;

import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import net.lim.controller.LauncherController;
import net.lim.model.adv.Advertisement;
import net.lim.model.adv.AdvertisementReceiver;
import net.lim.model.adv.StubAdvertisementReceiver;

import java.util.List;

/**
 * Pane containing news area (textflow in scrollpane) and a button to hide/show this area.
 */
public class NewsPane extends Pane {
    private LauncherController controller;
    private ScrollPane scrollPane;
    private TextFlow newsTextFlow;
    private Rectangle showHideNews;

    public NewsPane(LauncherController controller) {
        this.controller = controller;
        init();
    }
    private void init() {
        initShowHideButton();
        initScrollPane();
        initNewsFlow();
        addContent();
        this.relocate(0, 32);
        scrollPane.setContent(newsTextFlow);
        setOpacity(0.75);
    }

    public void postInit() {
        fillNewsFlow();
    }

    private void initShowHideButton() {
        showHideNews = new Rectangle();
        showHideNews.layoutXProperty().setValue(0);
        showHideNews.setWidth(64);
        showHideNews.heightProperty().bind(this.heightProperty());
        showHideNews.layoutYProperty().bind(this.layoutYProperty());
        showHideNews.setOnMouseClicked(e -> controller.hideNewsButtonPressed(scrollPane));
    }

    private void addContent() {
        getChildren().addAll(showHideNews, scrollPane);

    }

    private void fillNewsFlow() {
        controller.fillNewsFlow(this);
    }

    public void putNewToArea(Advertisement advertisement) {
        Text header = new Text(advertisement.getHeader() + "\n");
        header.setStyle("-fx-font-weight:bold; -fx-fill: darkgoldenrod");
        Text newsContent = new Text(advertisement.getText() + "\n");
        newsContent.setStyle("-fx-font-weight: normal; -fx-fill: white");

        if (advertisement.getUrl() != null) {
            Text url = new Text("Read more >>>" + "\n");
            url.setFill(Color.valueOf("1B78B8"));
            url.setCursor(Cursor.HAND);
            url.setStyle("-fx-font-weight: bold");
            url.setOnMouseClicked(e -> controller.linkPressed(advertisement.getUrl()));
            newsTextFlow.getChildren().add(0, url);
        }
        newsTextFlow.getChildren().add(0, newsContent);
        newsTextFlow.getChildren().add(0, header);
    }

    private void initNewsFlow() {
        newsTextFlow = new TextFlow();
        newsTextFlow.minHeightProperty().bind(scrollPane.heightProperty());
        newsTextFlow.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        newsTextFlow.textAlignmentProperty().setValue(TextAlignment.CENTER);
    }

    private void initScrollPane() {
        scrollPane = new ScrollPane();
        scrollPane.layoutXProperty().bind(this.layoutXProperty().add(showHideNews.widthProperty()));
        scrollPane.layoutYProperty().bind(this.layoutYProperty());
        scrollPane.maxHeightProperty().bind(this.heightProperty());
        scrollPane.minHeightProperty().bind(this.heightProperty());
        scrollPane.setPannable(true);
        scrollPane.setMinWidth(400);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.fitToWidthProperty().setValue(true);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
