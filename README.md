# FrameAllocationAlgorithms
Fourth project for operating systems.

Postępująca komplikacja zad. 3. Założyć, że: w systemie działa pewna ilość (rzędu ~10) procesów, każdy korzysta
z własnego zbioru stron (zasada lokalności wciąż obowiązuje), globalny ciąg odwołań jest wynikiem połączenia sekwencji
odwołań generowanych przez poszczególne procesy (każdy generuje ich wiele, nie jedną), zastępowanie stron odbywa się
zgodnie z LRU, każdemu system przydziela określoną liczbę ramek, na podstawie następujących metod:

1. Przydział proporcjonalny
2. Przydział równy
3. Sterowanie częstością błędów strony
4. Model strefowy
   
• Jak strategie przydziału ramek wpływają na wyniki (liczbę braków strony - globalnie, dla każdego procesu)?  
• Program powinien wypisywać na ekranie przyjęte założenia symulacji. Wymagana możliwość sterowania założeniami
symulacji (na poziomie kodu programu). 
