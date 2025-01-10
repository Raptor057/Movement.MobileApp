using Essency.Stock.Movement.Maui.Data;
using Essency.Stock.Movement.Maui.Interfaces;
using Essency.Stock.Movement.Maui.Repository;
using Essency.Stock.Movement.Maui.Services;
using Essency.Stock.Movement.Maui.Views.Pages.Login;
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

            builder.Logging.AddDebug();

            // Configura la conexión SQLite
            builder.Services.AddSingleton(_ => new SQLiteAsyncConnection(Constants.DatabasePath, Constants.Flags));

            // Registro de servicios
            builder.Services.AddSingleton<AppUsersDb>(); // Clase AppUsersDb
            builder.Services.AddSingleton<IAppUsers, AppUsersRepository>(); // Repositorio
            builder.Services.AddSingleton<Login>(); // Página Login

            // Registro del proveedor de logger
            builder.Services.AddSingleton<ILoggerProvider, SQLiteLoggerProvider>();

            // Configura el logger para usar el proveedor registrado
            var databaseProvider = builder.Services.BuildServiceProvider().GetRequiredService<SQLiteAsyncConnection>();
            builder.Logging.AddProvider(new SQLiteLoggerProvider(databaseProvider));

            return builder.Build();
        }
    }
}
