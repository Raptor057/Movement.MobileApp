using System.Security.Cryptography;
using System.Text;

namespace Essency.Stock.Movement.Maui.Services
{
    public class PasswordHasher
    {
        public static string HashPassword(string password)
        {
            // Convertir la contraseña en un arreglo de bytes
            byte[] bytes = Encoding.UTF8.GetBytes(password);

            // Crear un objeto SHA256
            using (SHA256 sha256 = SHA256.Create())
            {
                // Generar el hash
                byte[] hashBytes = sha256.ComputeHash(bytes);

                // Convertir el hash en un string hexadecimal
                StringBuilder hash = new StringBuilder();
                foreach (byte b in hashBytes)
                {
                    hash.Append(b.ToString("x2")); // Convertir cada byte a hexadecimal
                }

                return hash.ToString();
            }
        }
    }
}
