using Essency.Stock.Movement.Maui.Models;
using Essency.Stock.Movement.Maui.Models.AppConfigurations;
using Essency.Stock.Movement.Maui.Services;
using SQLite;

namespace Essency.Stock.Movement.Maui.Data
{
    public abstract class SQLiteConnection
    {
        protected SQLiteAsyncConnection Database;

        public async Task InitDatabase()
        {
            if (Database is not null)
                return;

            Database = new SQLiteAsyncConnection(Constants.DatabasePath, Constants.Flags);

            // Crear tablas automáticamente si no existen
            await Database.CreateTableAsync<AppConfigurationEmail>();
            await Database.CreateTableAsync<AppConfigurationRegularExpression>();
            await Database.CreateTableAsync<AppUsers>();
            await Database.CreateTableAsync<TraceabilityStockList>();
            await Database.CreateTableAsync<StockList>();
            await Database.CreateTableAsync<LogEntry>();

            // Seed del usuario administrador
            await SeedAdminUser();
        }

        private async Task SeedAdminUser()
        {
            try
            {
                var adminExists = await Database.Table<AppUsers>()
                                                .FirstOrDefaultAsync(u => u.UserName == "admin");

                if (adminExists == null)
                {
                    var adminUser = new AppUsers
                    {
                        UserName = "Admin",
                        Name = "Administrator",
                        LastName = "System",
                        Password = PasswordHasher.HashPassword("Admin123***"),
                        CreateUserDate = DateTime.UtcNow,
                        IsAdmin = true,
                        Enable = true
                    };

                    await Database.InsertAsync(adminUser);
                }
            }
            catch (Exception ex)
            {
                throw new Exception($"Error seeding admin user: {ex.Message}");
            }
        }
    }
}
