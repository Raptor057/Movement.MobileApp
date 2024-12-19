using Essency.Stock.Movement.Maui.Data;
using Microsoft.Extensions.Logging;


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
            builder.Services.AddSingleton<EssencyStockMovementSqLite>();
            builder.Services.AddTransient<EssencyStockMovementSqLite>();

            builder.Services.AddSingleton<EssencyStockMovementSqLite>();
#else

#endif

            return builder.Build();
        }
    }
}
