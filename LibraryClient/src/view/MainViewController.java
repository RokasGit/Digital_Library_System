package view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import viewmodel.BookModel;
import viewmodel.MainViewModel;

public class MainViewController {
    @FXML private Label username_spot;
    @FXML private TextField search_textfield;
    @FXML private TableView<BookModel> main_table;
    @FXML private TableColumn<BookModel, String> BookIDCollum;
    @FXML private TableColumn<BookModel, String> BookNameCollum;
    @FXML private TableColumn<BookModel, String> BookAuthorCollum;
    @FXML private TableColumn<BookModel, String> YearPublishedCollum;
    @FXML private TableColumn<BookModel, String> RatingCollum;



    private Region root;
    private MainViewModel mainViewModel;
    private ViewHandler viewHandler;
    private ViewState viewState;
    public MainViewController (){

    }

    public void init(ViewHandler viewHandler, MainViewModel mainViewModel, Region root,ViewState viewState){
        this.viewHandler = viewHandler;
        this.mainViewModel = mainViewModel;
        this.root = root;
        this.viewState=viewState;
        search_textfield.textProperty().bindBidirectional(mainViewModel.getSearch_textfield());
        username_spot.textProperty().bind(mainViewModel.getUsername_spot());

        BookIDCollum.setCellValueFactory(celldata -> celldata.getValue().getBookID());
        BookNameCollum.setCellValueFactory(celldata -> celldata.getValue().getBookName());
        BookAuthorCollum.setCellValueFactory(celldata -> celldata.getValue().getBookAuthor());
        YearPublishedCollum.setCellValueFactory(celldata -> celldata.getValue().getYearPublished());
        RatingCollum.setCellValueFactory(celldata -> celldata.getValue().getRating());

        //main_table.setItems(mainViewModel.getList());

        //CAUTION super code that does real time searching for everything form an indian guy on YT
        FilteredList<BookModel> filteredData = new FilteredList<>(mainViewModel.getList(), b -> true);

        search_textfield.textProperty().addListener(((observable, oldValue, newValue) ->{
            filteredData.setPredicate(book -> {
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if(book.getBookName().get().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else if (book.getBookID().get().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else if (book.getBookAuthor().get().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else if (book.getYearPublished().get().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                } else {
                    return false;
                }
            });
        } ));

        SortedList<BookModel> sortedList = new SortedList<>(filteredData);

        sortedList.comparatorProperty().bind(main_table.comparatorProperty());

        main_table.setItems(sortedList);



        //Double click to open book
        main_table.setRowFactory( tv -> {
            TableRow<BookModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (! row.isEmpty())){
                    BookModel rowData = row.getItem();
                    viewState.setSelectedBook(rowData.getBookID().get());
                    viewHandler.openView("edit");
                }
            });
            return row;
        });


    }

    public Region getRoot(){return root;}

    public void setName(){
        mainViewModel.setName();
    }

    public void reset(){mainViewModel.clear();}


    @FXML public void home_button(){
        viewHandler.openView("main");
    }

    @FXML public void borrow_button(){
        BookModel selectedItem = main_table.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
        mainViewModel.BorrowBook(selectedItem.getBookID().get());
    }else{
            throw new IllegalArgumentException("None of the books are selected.");
        }
    }

    @FXML public void inventory_button(){
        viewHandler.openView("inventory");
    }

    @FXML public void Profile_button(){viewHandler.openView("profile");}


}
