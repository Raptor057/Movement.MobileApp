using Microsoft.Extensions.Logging;
using SQLite;

namespace Essency.Stock.Movement.Maui.Services
{
    public class SQLiteLoggerProvider : ILoggerProvider
    {
        private readonly SQLiteAsyncConnection _database;

        public SQLiteLoggerProvider(SQLiteAsyncConnection database)
        {
            _database = database;
        }

        public ILogger CreateLogger(string categoryName)
        {
            return new SQLiteLogger(_database);
        }

        public void Dispose()
        {
            // Limpieza si es necesario
        }
    }

}
