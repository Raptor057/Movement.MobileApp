using Essency.Stock.Movement.Maui.Models.Configuration.Loggin;
using Microsoft.Extensions.Logging;
using SQLite;


namespace Essency.Stock.Movement.Maui.Services.Logger
{
    public class SQLiteLogger : ILogger
    {
        private readonly SQLiteAsyncConnection _database;

        public SQLiteLogger(SQLiteAsyncConnection database)
        {
            _database = database;
        }

        public IDisposable BeginScope<TState>(TState state) => null;

        public bool IsEnabled(LogLevel logLevel) => logLevel != LogLevel.None;

        public void Log<TState>(LogLevel logLevel, EventId eventId, TState state, Exception exception, Func<TState, Exception, string> formatter)
        {
            if (!IsEnabled(logLevel))
                return;

            var message = formatter(state, exception);

            // Guardar el log en la base de datos
            var logEntry = new LogEntry
            {
                Timestamp = DateTime.UtcNow,
                LogLevel = logLevel.ToString(),
                Message = message,
                Exception = exception?.ToString() ?? "N/A"
            };

            _database.InsertAsync(logEntry).Wait();
        }
    }
}
