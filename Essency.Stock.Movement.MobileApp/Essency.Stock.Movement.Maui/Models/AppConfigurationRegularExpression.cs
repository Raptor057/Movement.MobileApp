using SQLite;

namespace Essency.Stock.Movement.Maui.Models
{
    public class AppConfigurationRegularExpression
    {
        [PrimaryKey,AutoIncrement]
        public int ID { get; set; }
        
        [NotNull]
        public string NameRegularExpression { get; set; }

        [NotNull]
        public string RegularExpression { get; set; }
    }
}
