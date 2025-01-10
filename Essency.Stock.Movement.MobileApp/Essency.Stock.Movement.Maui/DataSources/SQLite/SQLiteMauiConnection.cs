using SQLite;
using Essency.Stock.Movement.Maui.Models;
using Essency.Stock.Movement.Maui.Models.Configuration;
using Essency.Stock.Movement.Maui.Services;
using Essency.Stock.Movement.Maui.Models.Configuration.Loggin;

namespace Essency.Stock.Movement.Maui.DataSources.SQLite
{
    public class SQLiteMauiConnection
    {
        private readonly SQLiteAsyncConnection _database;

        public SQLiteMauiConnection(SQLiteAsyncConnection database)
        {
            _database = database;
        }

        public async Task InitDatabaseAsync()
        {
            // Crear tablas si no existen
            await _database.CreateTableAsync<AppUsers>();
            await _database.CreateTableAsync<AppConfigurationEmail>();
            await _database.CreateTableAsync<AppConfigurationRegularExpression>();
            await _database.CreateTableAsync<StockList>();
            await _database.CreateTableAsync<TraceabilityStockList>();
            await _database.CreateTableAsync<LogEntry>();

            // Inicializar datos básicos
            await SeedAdminUserAsync();
        }

        private async Task SeedAdminUserAsync()
        {
            // Verificar si existe el usuario administrador
            var adminExists = await _database.Table<AppUsers>().FirstOrDefaultAsync(u => u.UserName == "admin");
            if (adminExists == null)
            {
                var adminUser = new AppUsers
                {
                    UserName = "Admin",
                    Name = "Administrator",
                    LastName = "System",
                    Password = PasswordHasher.HashPassword("Admin123***"), // Método para hash de contraseñas
                    CreateUserDate = DateTime.UtcNow,
                    IsAdmin = true,
                    Enable = true
                };

                await _database.InsertAsync(adminUser);
            }
        }
    }
}
