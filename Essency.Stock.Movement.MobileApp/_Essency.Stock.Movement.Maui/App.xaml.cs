using Essency.Stock.Movement.Maui.Data;

namespace Essency.Stock.Movement.Maui
{
    public partial class App : Application
    {
        private readonly AppUsersDb _db;

        public App(AppUsersDb db)
        {
            InitializeComponent();
            _db = db;

            // Inicializar la base de datos
            _db.InitDatabase().Wait();

            MainPage = new AppShell();
        }
    }

}
