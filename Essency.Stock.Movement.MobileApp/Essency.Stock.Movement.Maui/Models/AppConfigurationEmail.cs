using SQLite;

namespace Essency.Stock.Movement.Maui.Models
{
    public class AppConfigurationEmail
    {
        [Column("Sending To Email")]
        public string Email { get; set; }

    }
}
