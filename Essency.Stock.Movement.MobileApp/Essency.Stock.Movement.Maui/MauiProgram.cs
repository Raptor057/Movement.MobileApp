using Essency.Stock.Movement.Maui.DataSources.SQLite;
using Microsoft.Extensions.Logging;
using SQLite;


namespace Essency.Stock.Movement.Maui
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var builder = MauiApp.CreateBuilder();
            builder
                .UseMauiApp<App>()
                .ConfigureFonts(fonts =>
                {
                    fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                    fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
                });
#if DEBUG
            builder.Logging.AddDebug();
#endif

            // Configura la conexión SQLite
            builder.Services.AddSingleton(_ => new SQLiteAsyncConnection(Constants.DatabasePath, Constants.Flags));
            builder.Services.AddSingleton<SQLiteMauiConnection>();

            // Inicializar Plugin.LocalNotification
            //builder.UseLocalNotification();

            return builder.Build();

        }
    }
}
