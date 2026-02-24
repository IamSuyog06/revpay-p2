import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { NotificationService } from '../../core/services/notification.service';
import { Notification, NotificationPreference } from '../../models/notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationsComponent implements OnInit {

  notifications: Notification[] = [];
  preferences: NotificationPreference | null = null;
  activeTab: 'all' | 'unread' | 'preferences' = 'all';
  isLoading = true;

  constructor(
    private notificationService: NotificationService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.loadPreferences();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.notificationService.getAllNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadUnread(): void {
    this.isLoading = true;
    this.notificationService.getUnreadNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadPreferences(): void {
    this.notificationService.getPreferences().subscribe({
      next: (prefs) => {
        this.preferences = prefs;
        this.cdr.markForCheck();
      }
    });
  }

  setTab(tab: any): void {
    this.activeTab = tab;
    if (tab === 'all') this.loadNotifications();
    if (tab === 'unread') this.loadUnread();
    this.cdr.markForCheck();
  }

  onMarkAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.loadNotifications();
        this.cdr.markForCheck();
      }
    });
  }

  onMarkAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.toastr.success('All notifications marked as read');
        this.loadNotifications();
      }
    });
  }

  onSavePreferences(): void {
    if (!this.preferences) return;
    this.notificationService.updatePreferences(this.preferences).subscribe({
      next: () => {
        this.toastr.success('Preferences saved');
        this.cdr.markForCheck();
      },
      error: () => this.toastr.error('Failed to save preferences')
    });
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'TRANSACTION': return '💸';
      case 'MONEY_REQUEST': return '🤝';
      case 'CARD_CHANGE': return '💳';
      case 'LOW_BALANCE': return '⚠️';
      case 'INVOICE': return '🧾';
      case 'LOAN': return '💰';
      default: return '🔔';
    }
  }
}