namespace Essency.Stock.Movement.Maui
{
    public partial class MainPage : ContentPage
    {
        int count = 0;

        public MainPage()
        {
            InitializeComponent();
            
        }

        private async void OnCounterClicked(object sender, EventArgs e)
        {
            string dbPath = Constants.DatabasePath;

            // Enviar notificación
            //await DisplayAlert("Database Initialized", $"The database was created successfully at:\n{dbPath}","OK");

            count++;

            if (count == 1)
                CounterBtn.Text = $"Clicked {count} time";
            else
                CounterBtn.Text = $"Clicked {count} times";

            SemanticScreenReader.Announce(CounterBtn.Text);
        }
    }
}
