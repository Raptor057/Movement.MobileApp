using Essency.Stock.Movement.Maui.DataSources.SQLite;
using Essency.Stock.Movement.Maui.Models.Configuration;

namespace Essency.Stock.Movement.Maui.DataSources
{
    public class AppUsersDb
    {
        private readonly SQLiteMauiConnection _SqliteMaui;

        public AppUsersDb(SQLiteMauiConnection SqliteMaui)
        {
            _SqliteMaui = SqliteMaui;
        }

    }
}
