using Essency.Stock.Movement.Maui.DataSources.SQLite;
//using Plugin.LocalNotification;

namespace Essency.Stock.Movement.Maui
{
    public partial class App : Application
    {
        public App(SQLiteMauiConnection database)
        {
            InitializeComponent();

            // Inicializa la base de datos de forma asincrónica y envía una notificación
            Task.Run(async () =>
            {
                try
                {
                    await database.InitDatabaseAsync();
                    string dbPath = Constants.DatabasePath;

                    // Enviar notificación
                    //SendNotification("Database Initialized", $"The database was created successfully at:\n{dbPath}");
                }
                catch (Exception ex)
                {
                    // Enviar notificación de error
                    //SendNotification("Error", $"Failed to initialize the database:\n{ex.Message}");
                }
            });

            MainPage = new MainPage();
        }

        //private void SendNotification(string title, string message)
        //{
        //    var notification = new NotificationRequest
        //    {
        //        NotificationId = 1,
        //        Title = title,
        //        Description = message,
        //        ReturningData = "Dummy data", // Puedes usar esto para identificar la notificación
        //        Schedule = new NotificationRequestSchedule
        //        {
        //            NotifyTime = DateTime.Now.AddSeconds(1) // Notificar después de 1 segundo
        //        }
        //    };

        //    LocalNotificationCenter.Current.Show(notification);
        //}
    }
}
